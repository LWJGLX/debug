package org.lwjglx.debug.org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjglx.debug.openal.ALDevice;
import org.lwjglx.debug.openal.ALRT;
import org.lwjglx.debug.ResourceState;

public class ALC11 {

	public static long alcCaptureOpenDevice(ByteBuffer devicename, int frequency, int format, int buffersize) {
		long device = org.lwjgl.openal.ALC11.alcCaptureOpenDevice(devicename, frequency, format, buffersize);
		if (device != 0L) {
			ALDevice.create(device);
		}
		return device;
	}

	public static long alcCaptureOpenDevice(CharSequence devicename, int frequency, int format, int buffersize) {
		long device = org.lwjgl.openal.ALC11.alcCaptureOpenDevice(devicename, frequency, format, buffersize);
		if (device != 0L) {
			ALDevice.create(device);
		}
		return device;
	}

	public static boolean alcCaptureCloseDevice(long device) {
		if (device == 0L) {
			return false;
		}
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureCloseDevice");
		boolean res = org.lwjgl.openal.ALC11.alcCaptureCloseDevice(device);
		if (res && dev != null) {
			dev.state = ResourceState.DELETED;
		}
		return res;
	}

	public static void alcCaptureStart(long device) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureStart");
		org.lwjgl.openal.ALC11.alcCaptureStart(device);
	}

	public static void alcCaptureStop(long device) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureStop");
		org.lwjgl.openal.ALC11.alcCaptureStop(device);
	}

	public static void alcCaptureSamples(long device, ByteBuffer buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, ShortBuffer buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, IntBuffer buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, FloatBuffer buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, short[] buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, int[] buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}

	public static void alcCaptureSamples(long device, float[] buffer, int samples) {
		ALDevice dev = ALDevice.get(device);
		ALRT.checkDevice(dev, device, "alcCaptureSamples");
		org.lwjgl.openal.ALC11.alcCaptureSamples(device, buffer, samples);
	}
}
