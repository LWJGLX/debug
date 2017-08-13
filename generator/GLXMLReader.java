/*
 * (C) Copyright 2017 Kai Burjack

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class GLXMLReader extends DefaultHandler {

    static class Command {
        String name;
        List<Param> params = new ArrayList<>();
        Return returnType;
        Extension extension;
    }

    static class GLenum {
        String name;
        int value;
        boolean hasValue;
        boolean used;

        GLenum() {
        }

        GLenum(int value) {
            this.value = value;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            GLenum other = (GLenum) obj;
            if (value != other.value)
                return false;
            return true;
        }
    }

    static class Param {
        String name;
        String type;
        Group group;
    }

    static class Return {
        String type;
        Group group;
    }

    static class Group {
        String name;
        String type;
        Map<String, GLenum> enums = new HashMap<>();
    }

    static class Extension {
        String name;
        Map<String, GLenum> enums = new HashMap<>();
        Map<String, Command> commands = new HashMap<>();
    }

    private String path;
    private Stack<String> paths = new Stack<String>();
    {
        paths.add("");
    }
    private Group currentGroup;
    private Map<String, Group> groups = new HashMap<>();
    private Map<String, Command> commands = new HashMap<>();
    private Map<String, Extension> extensions = new HashMap<>();
    private Map<String, GLenum> enums = new HashMap<>();
    private Command currentCommand;
    private Param currentParam;
    private Extension currentExtension;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("/registry/commands/command/proto/name".equals(path)) {
            String name = new String(ch, start, length).trim();
            currentCommand.name = name;
            commands.put(name, currentCommand);
        } else if ("/registry/commands/command/proto/ptype".equals(path)) {
            String ptype = new String(ch, start, length).trim();
            Return ret = currentCommand.returnType;
            ret.type = ptype;
            // Patch up some GLboolean parameters which have no Boolean group
            if ("GLboolean".equals(ptype) && (ret.group == null || ret.group.name == null || !ret.group.name.equals("Boolean"))) {
                ret.group = groups.get("Boolean");
            }
        } else if ("/registry/commands/command/param/name".equals(path)) {
            String name = new String(ch, start, length).trim();
            currentParam.name = name;
        } else if ("/registry/commands/command/param/ptype".equals(path)) {
            String ptype = new String(ch, start, length).trim();
            currentParam.type = ptype;
            // Patch up some GLboolean parameters which have no Boolean group
            if ("GLboolean".equals(ptype) && (currentParam.group == null || currentParam.group.name == null || !currentParam.group.name.equals("Boolean"))) {
                currentParam.group = groups.get("Boolean");
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        path = paths.peek() + "/" + localName;
        paths.push(path);
        if ("/registry/groups/group".equals(path)) {
            currentGroup = new Group();
            currentGroup.name = attributes.getValue("name");
            groups.put(currentGroup.name, currentGroup);
        } else if ("/registry/groups/group/enum".equals(path)) {
            String enumName = attributes.getValue("name");
            GLenum e = enums.get(enumName);
            if (e == null) {
                e = new GLenum();
                e.name = enumName;
                enums.put(e.name, e);
            }
            currentGroup.enums.put(e.name, e);
        } else if ("/registry/enums".equals(path)) {
            // Use the null-group for /registry/enums.
            // Even though those elements have a "group" attribute, that
            // hardly ever matches any /registry/groups/group
            // example, see: "ShaderType"
            Group group = groups.get(null);
            if (group == null) {
                group = new Group();
                groups.put(group.name, group);
            }
            currentGroup = group;
        } else if ("/registry/enums/enum".equals(path)) {
            String hexValue = attributes.getValue("value");
            int value;
            if (hexValue.startsWith("0x")) {
                long lng;
                if (hexValue.equals("0xFFFFFFFFFFFFFFFF")) {
                    lng = -1L;
                } else {
                    lng = Long.parseLong(hexValue.substring(2), 16);
                }
                value = (int) (lng & 0xFFFFFFFF);
            } else {
                long lng = Long.parseLong(hexValue);
                value = (int) (lng & 0xFFFFFFFF);
            }
            String enumName = attributes.getValue("name");
            GLenum e = enums.get(enumName);
            if (e == null) {
                e = new GLenum();
                e.name = enumName;
            } else if (!e.name.equals(enumName)) {
                throw new AssertionError("Duplicate enum [" + e.name + "] and [" + enumName + "] = " + value);
            }
            // currenGroup here is always the null-group
            currentGroup.enums.put(e.name, e);
            /* Also add to null group */
            enums.put(e.name, e);
            e.value = value;
            e.hasValue = true;
        } else if ("/registry/commands/command".equals(path)) {
            currentCommand = new Command();
        } else if ("/registry/commands/command/proto".equals(path)) {
            String groupName = attributes.getValue("group");
            Return ret = new Return();
            currentCommand.returnType = ret;
            ret.group = groups.get(groupName);
        } else if ("/registry/commands/command/alias".equals(path)) {
            String aliasName = attributes.getValue("name");
            if (!commands.containsKey(aliasName))
                commands.put(aliasName, currentCommand);
        } else if ("/registry/commands/command/param".equals(path)) {
            Param param = new Param();
            currentParam = param;
            currentCommand.params.add(param);
            String groupName = attributes.getValue("group");
            if ("PixelInternalFormat".equals(groupName)) {
                /* The gl.xml is apparently handcrafted */
                groupName = "InternalFormat";
            }
            param.group = groups.get(groupName);
        } else if ("/registry/extensions/extension".equals(path)) {
            String name = attributes.getValue("name");
            currentExtension = new Extension();
            extensions.put(name, currentExtension);
            currentExtension.name = name;
        } else if ("/registry/extensions/extension/require/enum".equals(path)) {
            String enumName = attributes.getValue("name");
            currentExtension.enums.put(enumName, enums.get(enumName));
        } else if ("/registry/extensions/extension/require/command".equals(path)) {
            String commandName = attributes.getValue("name");
            Command cmd = commands.get(commandName);
            currentExtension.commands.put(commandName, cmd);
            if (cmd.extension != null) {
                /* More than one extension defines this command... sigh... */
                /* We merge both extension's enums */
                cmd.extension.enums.putAll(currentExtension.enums);
            } else {
                cmd.extension = currentExtension;
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            generate();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private void generate() throws Exception {
        StringBuilder sb = new StringBuilder();
        StringBuilder prolog = new StringBuilder();
        FileOutputStream fos = new FileOutputStream(new File("src/org/lwjglx/debug/GLmetadata.java"));
        Writer writer = new OutputStreamWriter(fos);
        prolog.append("package org.lwjglx.debug;\n");
        prolog.append("import java.util.Map;\n");
        prolog.append("import java.util.HashMap;\n");
        prolog.append("public class GLmetadata {\n");
        sb.append("  private static final int GLboolean = 1;\n");
        sb.append("  private static final int GLenum = 2;\n");
        sb.append("  private static final int GLbitfield = 3;\n");
        /* See which GLenum is actually used in a group*/
        for (Group group : groups.values()) {
            for (GLenum e : group.enums.values()) {
                if (!e.hasValue)
                    continue;
                if ((e.name.endsWith("_ARB") || e.name.endsWith("_EXT") || e.name.endsWith("_NV") || e.name.endsWith("_OES") || e.name.endsWith("_ATI")) && group.enums.containsValue(new GLenum(e.value))) {
                    continue;
                }
                e.used = true;
            }
        }
        /* See which GLenum is used in an extension */
        for (Extension ext : extensions.values()) {
            if (ext.commands.isEmpty())
                continue;
            /* Are there any commands referencing this extension? */
            boolean commandReferences = false;
            for (Command cmd : commands.values()) {
                if (cmd.extension == ext) {
                    commandReferences = true;
                    break;
                }
            }
            if (!commandReferences)
                continue;
            /* Does this extension has any commands we need to resolve parameters of? */
            boolean hasCommands = false;
            for (Command cmd : ext.commands.values()) {
                for (Param param : cmd.params) {
                    if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                        hasCommands = true;
                        break;
                    }
                }
                if ("GLboolean".equals(cmd.returnType.type) || "GLenum".equals(cmd.returnType.type) || "GLbitfield".equals(cmd.returnType.type)) {
                    hasCommands = true;
                }
            }
            if (!hasCommands) {
                continue;
            }
            for (GLenum e : ext.enums.values()) {
                if (!e.hasValue)
                    continue;
                e.used = true;
            }
        }
        /* Print used GLenums */
        for (GLenum e : enums.values()) {
            if (!e.hasValue || !e.used)
                continue;
            sb.append("  private static final int ").append(e.name).append(" = ").append(e.value).append(";\n");
        }
        for (Group group : groups.values()) {
            String gname = group.name;
            if (gname == null) {
                gname = "_null_";
            }
            int numEnums = 0;
            Iterator<GLenum> it = group.enums.values().iterator();
            while (it.hasNext()) {
                GLenum e = it.next();
                if (!e.hasValue)
                    continue;
                if ((e.name.endsWith("_ARB") || e.name.endsWith("_EXT") || e.name.endsWith("_NV") || e.name.endsWith("_OES") || e.name.endsWith("_ATI")) && group.enums.containsValue(new GLenum(e.value))) {
                    continue;
                }
                numEnums++;
            }
            sb.append("  private static Map<Integer, String> ").append(gname).append(";\n");
            int i = 0;
            it = group.enums.values().iterator();
            StringBuilder groupInit = new StringBuilder();
            do {
                sb.append("  private static void ").append(gname).append(i).append("() {\n");
                groupInit.append("    ").append(gname).append(i).append("();\n");
                for (int t = 0; it.hasNext() && t < 100; t++) {
                    GLenum e = it.next();
                    if (!e.hasValue)
                        continue;
                    if ((e.name.endsWith("_ARB") || e.name.endsWith("_EXT") || e.name.endsWith("_NV") || e.name.endsWith("_OES") || e.name.endsWith("_ATI")) && group.enums.containsValue(new GLenum(e.value))) {
                        continue;
                    }
                    sb.append("    ").append(gname).append(".put(").append(e.name).append(", \"").append(e.name).append("\");\n");
                }
                sb.append("  }\n");
                i++;
            } while (it.hasNext());
            sb.append("  public static Map<Integer, String> ").append(gname).append("() {\n");
            sb.append("    if (").append(gname).append(" != null)\n");
            sb.append("      return ").append(gname).append(";\n");
            sb.append("    ").append(gname).append(" = new HashMap<Integer, String>(").append(numEnums).append(");\n");
            sb.append(groupInit.toString());
            sb.append("    return ").append(gname).append(";\n");
            sb.append("  }\n");
        }
        for (Extension ext : extensions.values()) {
            if (ext.enums.isEmpty() || ext.commands.isEmpty())
                continue;
            boolean hasCommands = false;
            /* Does this extension has any commands we need to resolve parameters of? */
            for (Command cmd : ext.commands.values()) {
                for (Param param : cmd.params) {
                    if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                        hasCommands = true;
                        break;
                    }
                }
                if ("GLboolean".equals(cmd.returnType.type) || "GLenum".equals(cmd.returnType.type) || "GLbitfield".equals(cmd.returnType.type)) {
                    hasCommands = true;
                }
            }
            if (!hasCommands) {
                continue;
            }
            /* Are there any commands referencing this extension? */
            boolean commandReferences = false;
            for (Command cmd : commands.values()) {
                boolean found = false;
                for (Param param : cmd.params) {
                    if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                        found = true;
                        break;
                    }
                }
                if ("GLboolean".equals(cmd.returnType.type) || "GLenum".equals(cmd.returnType.type) || "GLbitfield".equals(cmd.returnType.type)) {
                    found = true;
                }
                if (!found)
                    continue;
                if (cmd.extension == ext) {
                    commandReferences = true;
                    break;
                }
            }
            if (!commandReferences)
                continue;
            int numEnums = 0;
            for (GLenum e : ext.enums.values()) {
                if (!e.hasValue)
                    continue;
                numEnums++;
            }
            sb.append("  private static Map<Integer, String> ").append(ext.name).append(";\n");
            sb.append("  private static Map<Integer, String> ").append(ext.name).append("() {\n");
            sb.append("    if (").append(ext.name).append(" != null)\n");
            sb.append("      return ").append(ext.name).append(";\n");
            sb.append("    ").append(ext.name).append(" = new HashMap<Integer, String>(").append(numEnums).append(");\n");
            for (GLenum e : ext.enums.values()) {
                if (!e.hasValue)
                    continue;
                sb.append("    ").append(ext.name).append(".put(").append(e.name).append(", \"").append(e.name).append("\");\n");
            }
            sb.append("    return ").append(ext.name).append(";\n");
            sb.append("  }\n");
        }
        for (Command cmd : commands.values()) {
            boolean found = false;
            for (Param param : cmd.params) {
                if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                    found = true;
                    break;
                }
            }
            if ("GLboolean".equals(cmd.returnType.type) || "GLenum".equals(cmd.returnType.type) || "GLbitfield".equals(cmd.returnType.type)) {
                found = true;
            }
            if (!found)
                continue;
            int numParams = 0;
            for (Param param : cmd.params) {
                if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                    if (param.group == null || param.group.name == null) {
                        numParams++;
                    } else {
                        numParams++;
                    }
                }
            }
            sb.append("  private static Command ").append(cmd.name).append(";\n");
            sb.append("  public static Command ").append(cmd.name).append("() {\n");
            sb.append("    if (").append(cmd.name).append(" != null)\n");
            sb.append("      return ").append(cmd.name).append(";\n");
            sb.append("    Command cmd = new Command(").append(numParams).append(");\n");
            if (cmd.returnType.group != null && cmd.returnType.group.name != null) {
                sb.append("    cmd.returnGroup = ").append(cmd.returnType.group.name).append("();\n");
            } else {
                sb.append("    cmd.returnGroup = _null_();\n");
            }
            for (Param param : cmd.params) {
                if ("GLboolean".equals(param.type) || "GLenum".equals(param.type) || "GLbitfield".equals(param.type)) {
                    if (param.group == null || param.group.name == null) {
                        sb.append("    cmd.addParam(\"").append(param.name).append("\", ").append(param.type).append(", _null_());\n");
                    } else {
                        sb.append("    cmd.addParam(\"").append(param.name).append("\", ").append(param.type).append(", ").append(param.group.name).append("());\n");
                    }
                }
            }
            if (cmd.extension != null && !cmd.extension.enums.isEmpty()) {
                sb.append("    cmd.extension = ").append(cmd.extension.name).append("();\n");
            }
            sb.append("    ").append(cmd.name).append(" = cmd;\n");
            sb.append("    return cmd;\n");
            sb.append("  }\n");
        }
        writer.write(prolog.toString());
        writer.write(sb.toString());
        writer.write("}\n");
        writer.close();
        fos.close();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        paths.pop();
        path = paths.peek();
    }

    /**
     * https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html
     */
    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }

    public static void main(String[] args) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new GLXMLReader());
        xmlReader.parse(convertToFileURL("spec/gl.xml"));
    }

}
