package org.lwjglx.debug.org.lwjgl.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjglx.debug.Properties;
import org.lwjglx.debug.RT;
import org.lwjglx.debug.openal.ALContext;
import org.lwjglx.debug.openal.ALObjects;
import org.lwjglx.debug.openal.ALRT;
import org.lwjglx.debug.ResourceState;

public class EXTEfx {

	/* --- Effect Objects --- */

	public static int alGenEffects() {
		ALContext ctx = ALRT.checkContext("alGenEffects");
		int effect = org.lwjgl.openal.EXTEfx.alGenEffects();
		if (ctx != null && effect != 0) {
			ctx.effects.put(effect, new ALObjects.Effect(effect, ctx));
		}
		return effect;
	}

	public static void alGenEffects(IntBuffer effects) {
		ALContext ctx = ALRT.checkContext("alGenEffects");
		int pos = effects.position();
		org.lwjgl.openal.EXTEfx.alGenEffects(effects);
		if (ctx != null) {
			for (int i = pos; i < effects.limit(); i++) {
				int e = effects.get(i);
				if (e != 0) {
					ctx.effects.put(e, new ALObjects.Effect(e, ctx));
				}
			}
		}
	}

	public static void alGenEffects(int[] effects) {
		ALContext ctx = ALRT.checkContext("alGenEffects");
		org.lwjgl.openal.EXTEfx.alGenEffects(effects);
		if (ctx != null) {
			for (int e : effects) {
				if (e != 0) {
					ctx.effects.put(e, new ALObjects.Effect(e, ctx));
				}
			}
		}
	}

	public static void alDeleteEffects(int effect) {
		ALContext ctx = ALRT.checkContext("alDeleteEffects");
		if (ctx != null && effect != 0) {
			deleteEffectCheck(ctx, effect);
		}
		org.lwjgl.openal.EXTEfx.alDeleteEffects(effect);
	}

	public static void alDeleteEffects(IntBuffer effects) {
		ALContext ctx = ALRT.checkContext("alDeleteEffects");
		if (ctx != null) {
			int pos = effects.position();
			for (int i = pos; i < effects.limit(); i++) {
				int e = effects.get(i);
				if (e != 0) {
					deleteEffectCheck(ctx, e);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteEffects(effects);
	}

	public static void alDeleteEffects(int[] effects) {
		ALContext ctx = ALRT.checkContext("alDeleteEffects");
		if (ctx != null) {
			for (int e : effects) {
				if (e != 0) {
					deleteEffectCheck(ctx, e);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteEffects(effects);
	}

	private static void deleteEffectCheck(ALContext ctx, int effect) {
		ALObjects.Effect e = ctx.effects.get(effect);
		if (e == null) {
			RT.throwISEOrLogError("alDeleteEffects: unknown effect " + effect);
			return;
		}
		if (e.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alDeleteEffects: effect " + effect + " already deleted (double-free)"
					+ ALRT.deletionInfo(e.deletionSite) + ALRT.creationInfo(e.creationSite));
			return;
		}
		e.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			e.deletionSite = new Throwable("Effect deleted here");
		}
	}

	public static boolean alIsEffect(int effect) {
		ALRT.checkContext("alIsEffect");
		return org.lwjgl.openal.EXTEfx.alIsEffect(effect);
	}

	public static void alEffecti(int effect, int param, int value) {
		ALContext ctx = ALRT.checkContext("alEffecti");
		ALRT.checkEffect(ctx, effect, "alEffecti");
		org.lwjgl.openal.EXTEfx.alEffecti(effect, param, value);
	}

	public static void alEffectiv(int effect, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alEffectiv");
		ALRT.checkEffect(ctx, effect, "alEffectiv");
		org.lwjgl.openal.EXTEfx.alEffectiv(effect, param, values);
	}

	public static void alEffectiv(int effect, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alEffectiv");
		ALRT.checkEffect(ctx, effect, "alEffectiv");
		org.lwjgl.openal.EXTEfx.alEffectiv(effect, param, values);
	}

	public static void alEffectf(int effect, int param, float value) {
		ALContext ctx = ALRT.checkContext("alEffectf");
		ALRT.checkEffect(ctx, effect, "alEffectf");
		org.lwjgl.openal.EXTEfx.alEffectf(effect, param, value);
	}

	public static void alEffectfv(int effect, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alEffectfv");
		ALRT.checkEffect(ctx, effect, "alEffectfv");
		org.lwjgl.openal.EXTEfx.alEffectfv(effect, param, values);
	}

	public static void alEffectfv(int effect, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alEffectfv");
		ALRT.checkEffect(ctx, effect, "alEffectfv");
		org.lwjgl.openal.EXTEfx.alEffectfv(effect, param, values);
	}

	public static int alGetEffecti(int effect, int param) {
		ALContext ctx = ALRT.checkContext("alGetEffecti");
		ALRT.checkEffect(ctx, effect, "alGetEffecti");
		return org.lwjgl.openal.EXTEfx.alGetEffecti(effect, param);
	}

	public static void alGetEffecti(int effect, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetEffecti");
		ALRT.checkEffect(ctx, effect, "alGetEffecti");
		org.lwjgl.openal.EXTEfx.alGetEffecti(effect, param, values);
	}

	public static void alGetEffecti(int effect, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetEffecti");
		ALRT.checkEffect(ctx, effect, "alGetEffecti");
		org.lwjgl.openal.EXTEfx.alGetEffecti(effect, param, values);
	}

	public static void alGetEffectiv(int effect, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetEffectiv");
		ALRT.checkEffect(ctx, effect, "alGetEffectiv");
		org.lwjgl.openal.EXTEfx.alGetEffectiv(effect, param, values);
	}

	public static void alGetEffectiv(int effect, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetEffectiv");
		ALRT.checkEffect(ctx, effect, "alGetEffectiv");
		org.lwjgl.openal.EXTEfx.alGetEffectiv(effect, param, values);
	}

	public static float alGetEffectf(int effect, int param) {
		ALContext ctx = ALRT.checkContext("alGetEffectf");
		ALRT.checkEffect(ctx, effect, "alGetEffectf");
		return org.lwjgl.openal.EXTEfx.alGetEffectf(effect, param);
	}

	public static void alGetEffectf(int effect, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetEffectf");
		ALRT.checkEffect(ctx, effect, "alGetEffectf");
		org.lwjgl.openal.EXTEfx.alGetEffectf(effect, param, values);
	}

	public static void alGetEffectf(int effect, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetEffectf");
		ALRT.checkEffect(ctx, effect, "alGetEffectf");
		org.lwjgl.openal.EXTEfx.alGetEffectf(effect, param, values);
	}

	public static void alGetEffectfv(int effect, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetEffectfv");
		ALRT.checkEffect(ctx, effect, "alGetEffectfv");
		org.lwjgl.openal.EXTEfx.alGetEffectfv(effect, param, values);
	}

	public static void alGetEffectfv(int effect, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetEffectfv");
		ALRT.checkEffect(ctx, effect, "alGetEffectfv");
		org.lwjgl.openal.EXTEfx.alGetEffectfv(effect, param, values);
	}

	/* --- Filter Objects --- */

	public static int alGenFilters() {
		ALContext ctx = ALRT.checkContext("alGenFilters");
		int filter = org.lwjgl.openal.EXTEfx.alGenFilters();
		if (ctx != null && filter != 0) {
			ctx.filters.put(filter, new ALObjects.Filter(filter, ctx));
		}
		return filter;
	}

	public static void alGenFilters(IntBuffer filters) {
		ALContext ctx = ALRT.checkContext("alGenFilters");
		int pos = filters.position();
		org.lwjgl.openal.EXTEfx.alGenFilters(filters);
		if (ctx != null) {
			for (int i = pos; i < filters.limit(); i++) {
				int f = filters.get(i);
				if (f != 0) {
					ctx.filters.put(f, new ALObjects.Filter(f, ctx));
				}
			}
		}
	}

	public static void alGenFilters(int[] filters) {
		ALContext ctx = ALRT.checkContext("alGenFilters");
		org.lwjgl.openal.EXTEfx.alGenFilters(filters);
		if (ctx != null) {
			for (int f : filters) {
				if (f != 0) {
					ctx.filters.put(f, new ALObjects.Filter(f, ctx));
				}
			}
		}
	}

	public static void alDeleteFilters(int filter) {
		ALContext ctx = ALRT.checkContext("alDeleteFilters");
		if (ctx != null && filter != 0) {
			deleteFilterCheck(ctx, filter);
		}
		org.lwjgl.openal.EXTEfx.alDeleteFilters(filter);
	}

	public static void alDeleteFilters(IntBuffer filters) {
		ALContext ctx = ALRT.checkContext("alDeleteFilters");
		if (ctx != null) {
			int pos = filters.position();
			for (int i = pos; i < filters.limit(); i++) {
				int f = filters.get(i);
				if (f != 0) {
					deleteFilterCheck(ctx, f);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteFilters(filters);
	}

	public static void alDeleteFilters(int[] filters) {
		ALContext ctx = ALRT.checkContext("alDeleteFilters");
		if (ctx != null) {
			for (int f : filters) {
				if (f != 0) {
					deleteFilterCheck(ctx, f);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteFilters(filters);
	}

	private static void deleteFilterCheck(ALContext ctx, int filter) {
		ALObjects.Filter f = ctx.filters.get(filter);
		if (f == null) {
			RT.throwISEOrLogError("alDeleteFilters: unknown filter " + filter);
			return;
		}
		if (f.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alDeleteFilters: filter " + filter + " already deleted (double-free)"
					+ ALRT.deletionInfo(f.deletionSite) + ALRT.creationInfo(f.creationSite));
			return;
		}
		f.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			f.deletionSite = new Throwable("Filter deleted here");
		}
	}

	public static boolean alIsFilter(int filter) {
		ALRT.checkContext("alIsFilter");
		return org.lwjgl.openal.EXTEfx.alIsFilter(filter);
	}

	public static void alFilteri(int filter, int param, int value) {
		ALContext ctx = ALRT.checkContext("alFilteri");
		ALRT.checkFilter(ctx, filter, "alFilteri");
		org.lwjgl.openal.EXTEfx.alFilteri(filter, param, value);
	}

	public static void alFilteriv(int filter, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alFilteriv");
		ALRT.checkFilter(ctx, filter, "alFilteriv");
		org.lwjgl.openal.EXTEfx.alFilteriv(filter, param, values);
	}

	public static void alFilteriv(int filter, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alFilteriv");
		ALRT.checkFilter(ctx, filter, "alFilteriv");
		org.lwjgl.openal.EXTEfx.alFilteriv(filter, param, values);
	}

	public static void alFilterf(int filter, int param, float value) {
		ALContext ctx = ALRT.checkContext("alFilterf");
		ALRT.checkFilter(ctx, filter, "alFilterf");
		org.lwjgl.openal.EXTEfx.alFilterf(filter, param, value);
	}

	public static void alFilterfv(int filter, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alFilterfv");
		ALRT.checkFilter(ctx, filter, "alFilterfv");
		org.lwjgl.openal.EXTEfx.alFilterfv(filter, param, values);
	}

	public static void alFilterfv(int filter, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alFilterfv");
		ALRT.checkFilter(ctx, filter, "alFilterfv");
		org.lwjgl.openal.EXTEfx.alFilterfv(filter, param, values);
	}

	public static int alGetFilteri(int filter, int param) {
		ALContext ctx = ALRT.checkContext("alGetFilteri");
		ALRT.checkFilter(ctx, filter, "alGetFilteri");
		return org.lwjgl.openal.EXTEfx.alGetFilteri(filter, param);
	}

	public static void alGetFilteri(int filter, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetFilteri");
		ALRT.checkFilter(ctx, filter, "alGetFilteri");
		org.lwjgl.openal.EXTEfx.alGetFilteri(filter, param, values);
	}

	public static void alGetFilteri(int filter, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetFilteri");
		ALRT.checkFilter(ctx, filter, "alGetFilteri");
		org.lwjgl.openal.EXTEfx.alGetFilteri(filter, param, values);
	}

	public static void alGetFilteriv(int filter, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetFilteriv");
		ALRT.checkFilter(ctx, filter, "alGetFilteriv");
		org.lwjgl.openal.EXTEfx.alGetFilteriv(filter, param, values);
	}

	public static void alGetFilteriv(int filter, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetFilteriv");
		ALRT.checkFilter(ctx, filter, "alGetFilteriv");
		org.lwjgl.openal.EXTEfx.alGetFilteriv(filter, param, values);
	}

	public static float alGetFilterf(int filter, int param) {
		ALContext ctx = ALRT.checkContext("alGetFilterf");
		ALRT.checkFilter(ctx, filter, "alGetFilterf");
		return org.lwjgl.openal.EXTEfx.alGetFilterf(filter, param);
	}

	public static void alGetFilterf(int filter, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetFilterf");
		ALRT.checkFilter(ctx, filter, "alGetFilterf");
		org.lwjgl.openal.EXTEfx.alGetFilterf(filter, param, values);
	}

	public static void alGetFilterf(int filter, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetFilterf");
		ALRT.checkFilter(ctx, filter, "alGetFilterf");
		org.lwjgl.openal.EXTEfx.alGetFilterf(filter, param, values);
	}

	public static void alGetFilterfv(int filter, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetFilterfv");
		ALRT.checkFilter(ctx, filter, "alGetFilterfv");
		org.lwjgl.openal.EXTEfx.alGetFilterfv(filter, param, values);
	}

	public static void alGetFilterfv(int filter, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetFilterfv");
		ALRT.checkFilter(ctx, filter, "alGetFilterfv");
		org.lwjgl.openal.EXTEfx.alGetFilterfv(filter, param, values);
	}

	/* --- Auxiliary Effect Slot Objects --- */

	public static int alGenAuxiliaryEffectSlots() {
		ALContext ctx = ALRT.checkContext("alGenAuxiliaryEffectSlots");
		int slot = org.lwjgl.openal.EXTEfx.alGenAuxiliaryEffectSlots();
		if (ctx != null && slot != 0) {
			ctx.auxSlots.put(slot, new ALObjects.AuxiliaryEffectSlot(slot, ctx));
		}
		return slot;
	}

	public static void alGenAuxiliaryEffectSlots(IntBuffer slots) {
		ALContext ctx = ALRT.checkContext("alGenAuxiliaryEffectSlots");
		int pos = slots.position();
		org.lwjgl.openal.EXTEfx.alGenAuxiliaryEffectSlots(slots);
		if (ctx != null) {
			for (int i = pos; i < slots.limit(); i++) {
				int s = slots.get(i);
				if (s != 0) {
					ctx.auxSlots.put(s, new ALObjects.AuxiliaryEffectSlot(s, ctx));
				}
			}
		}
	}

	public static void alGenAuxiliaryEffectSlots(int[] slots) {
		ALContext ctx = ALRT.checkContext("alGenAuxiliaryEffectSlots");
		org.lwjgl.openal.EXTEfx.alGenAuxiliaryEffectSlots(slots);
		if (ctx != null) {
			for (int s : slots) {
				if (s != 0) {
					ctx.auxSlots.put(s, new ALObjects.AuxiliaryEffectSlot(s, ctx));
				}
			}
		}
	}

	public static void alDeleteAuxiliaryEffectSlots(int slot) {
		ALContext ctx = ALRT.checkContext("alDeleteAuxiliaryEffectSlots");
		if (ctx != null && slot != 0) {
			deleteAuxSlotCheck(ctx, slot);
		}
		org.lwjgl.openal.EXTEfx.alDeleteAuxiliaryEffectSlots(slot);
	}

	public static void alDeleteAuxiliaryEffectSlots(IntBuffer slots) {
		ALContext ctx = ALRT.checkContext("alDeleteAuxiliaryEffectSlots");
		if (ctx != null) {
			int pos = slots.position();
			for (int i = pos; i < slots.limit(); i++) {
				int s = slots.get(i);
				if (s != 0) {
					deleteAuxSlotCheck(ctx, s);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteAuxiliaryEffectSlots(slots);
	}

	public static void alDeleteAuxiliaryEffectSlots(int[] slots) {
		ALContext ctx = ALRT.checkContext("alDeleteAuxiliaryEffectSlots");
		if (ctx != null) {
			for (int s : slots) {
				if (s != 0) {
					deleteAuxSlotCheck(ctx, s);
				}
			}
		}
		org.lwjgl.openal.EXTEfx.alDeleteAuxiliaryEffectSlots(slots);
	}

	private static void deleteAuxSlotCheck(ALContext ctx, int slot) {
		ALObjects.AuxiliaryEffectSlot a = ctx.auxSlots.get(slot);
		if (a == null) {
			RT.throwISEOrLogError("alDeleteAuxiliaryEffectSlots: unknown auxiliary effect slot " + slot);
			return;
		}
		if (a.state == ResourceState.DELETED) {
			RT.throwISEOrLogError("alDeleteAuxiliaryEffectSlots: auxiliary effect slot " + slot
					+ " already deleted (double-free)"
					+ ALRT.deletionInfo(a.deletionSite) + ALRT.creationInfo(a.creationSite));
			return;
		}
		a.state = ResourceState.DELETED;
		if (Properties.STRICT.enabled) {
			a.deletionSite = new Throwable("Auxiliary effect slot deleted here");
		}
	}

	public static boolean alIsAuxiliaryEffectSlot(int slot) {
		ALRT.checkContext("alIsAuxiliaryEffectSlot");
		return org.lwjgl.openal.EXTEfx.alIsAuxiliaryEffectSlot(slot);
	}

	public static void alAuxiliaryEffectSloti(int slot, int param, int value) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSloti");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSloti");
		if (param == org.lwjgl.openal.EXTEfx.AL_EFFECTSLOT_EFFECT && value != 0) {
			ALRT.checkEffect(ctx, value, "alAuxiliaryEffectSloti(AL_EFFECTSLOT_EFFECT)");
		}
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSloti(slot, param, value);
	}

	public static void alAuxiliaryEffectSlotiv(int slot, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSlotiv");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSlotiv");
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSlotiv(slot, param, values);
	}

	public static void alAuxiliaryEffectSlotiv(int slot, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSlotiv");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSlotiv");
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSlotiv(slot, param, values);
	}

	public static void alAuxiliaryEffectSlotf(int slot, int param, float value) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSlotf");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSlotf");
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSlotf(slot, param, value);
	}

	public static void alAuxiliaryEffectSlotfv(int slot, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSlotfv");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSlotfv");
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSlotfv(slot, param, values);
	}

	public static void alAuxiliaryEffectSlotfv(int slot, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alAuxiliaryEffectSlotfv");
		ALRT.checkAuxSlot(ctx, slot, "alAuxiliaryEffectSlotfv");
		org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSlotfv(slot, param, values);
	}

	public static int alGetAuxiliaryEffectSloti(int slot, int param) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSloti");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSloti");
		return org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSloti(slot, param);
	}

	public static void alGetAuxiliaryEffectSloti(int slot, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSloti");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSloti");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSloti(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSloti(int slot, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSloti");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSloti");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSloti(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSlotiv(int slot, int param, IntBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotiv");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotiv");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotiv(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSlotiv(int slot, int param, int[] values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotiv");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotiv");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotiv(slot, param, values);
	}

	public static float alGetAuxiliaryEffectSlotf(int slot, int param) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotf");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotf");
		return org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotf(slot, param);
	}

	public static void alGetAuxiliaryEffectSlotf(int slot, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotf");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotf");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotf(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSlotf(int slot, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotf");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotf");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotf(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSlotfv(int slot, int param, FloatBuffer values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotfv");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotfv");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotfv(slot, param, values);
	}

	public static void alGetAuxiliaryEffectSlotfv(int slot, int param, float[] values) {
		ALContext ctx = ALRT.checkContext("alGetAuxiliaryEffectSlotfv");
		ALRT.checkAuxSlot(ctx, slot, "alGetAuxiliaryEffectSlotfv");
		org.lwjgl.openal.EXTEfx.alGetAuxiliaryEffectSlotfv(slot, param, values);
	}
}
