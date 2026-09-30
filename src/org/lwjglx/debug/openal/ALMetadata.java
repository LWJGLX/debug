package org.lwjglx.debug.openal;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps OpenAL enum values and error codes to human-readable strings.
 * Hand-written since the OpenAL enum space is small enough to not need a generator.
 */
public class ALMetadata {

	private static final Map<Integer, String> AL_ENUMS = new HashMap<>();
	private static final Map<Integer, String> AL_ERRORS = new HashMap<>();
	private static final Map<Integer, String> ALC_ERRORS = new HashMap<>();

	static {
		/* AL errors */
		AL_ERRORS.put(0x0000, "AL_NO_ERROR");
		AL_ERRORS.put(0xA001, "AL_INVALID_NAME");
		AL_ERRORS.put(0xA002, "AL_INVALID_ENUM");
		AL_ERRORS.put(0xA003, "AL_INVALID_VALUE");
		AL_ERRORS.put(0xA004, "AL_INVALID_OPERATION");
		AL_ERRORS.put(0xA005, "AL_OUT_OF_MEMORY");

		/* ALC errors */
		ALC_ERRORS.put(0x0000, "ALC_NO_ERROR");
		ALC_ERRORS.put(0xA001, "ALC_INVALID_DEVICE");
		ALC_ERRORS.put(0xA002, "ALC_INVALID_CONTEXT");
		ALC_ERRORS.put(0xA003, "ALC_INVALID_ENUM");
		ALC_ERRORS.put(0xA004, "ALC_INVALID_VALUE");
		ALC_ERRORS.put(0xA006, "ALC_OUT_OF_MEMORY");

		/* AL error codes (also enums) */
		e(0x0000, "AL_NO_ERROR");
		e(0xA001, "AL_INVALID_NAME");
		e(0xA002, "AL_INVALID_ENUM");
		e(0xA003, "AL_INVALID_VALUE");
		e(0xA004, "AL_INVALID_OPERATION");
		e(0xA005, "AL_OUT_OF_MEMORY");

		/* AL boolean / state */
		e(0x0001, "AL_TRUE");

		/* Source properties */
		e(0x1001, "AL_SOURCE_RELATIVE");
		e(0x1002, "AL_CONE_INNER_ANGLE");
		e(0x1003, "AL_CONE_OUTER_ANGLE");
		e(0x1004, "AL_PITCH");
		e(0x1005, "AL_POSITION");
		e(0x1006, "AL_DIRECTION");
		e(0x1007, "AL_VELOCITY");
		e(0x1008, "AL_LOOPING");
		e(0x1009, "AL_BUFFER");
		e(0x100A, "AL_GAIN");
		e(0x100B, "AL_MIN_GAIN");
		e(0x100C, "AL_MAX_GAIN");
		e(0x100D, "AL_ORIENTATION");

		/* Source state */
		e(0x1010, "AL_SOURCE_STATE");
		e(0x1011, "AL_INITIAL");
		e(0x1012, "AL_PLAYING");
		e(0x1013, "AL_PAUSED");
		e(0x1014, "AL_STOPPED");
		e(0x1015, "AL_BUFFERS_QUEUED");
		e(0x1016, "AL_BUFFERS_PROCESSED");

		/* Source type */
		e(0x1027, "AL_SOURCE_TYPE");
		e(0x1028, "AL_STATIC");
		e(0x1029, "AL_STREAMING");
		e(0x1030, "AL_UNDETERMINED");

		/* Source distance */
		e(0x1020, "AL_REFERENCE_DISTANCE");
		e(0x1021, "AL_ROLLOFF_FACTOR");
		e(0x1022, "AL_CONE_OUTER_GAIN");
		e(0x1023, "AL_MAX_DISTANCE");
		e(0x1024, "AL_SEC_OFFSET");
		e(0x1025, "AL_SAMPLE_OFFSET");
		e(0x1026, "AL_BYTE_OFFSET");

		/* Buffer format */
		e(0x1100, "AL_FORMAT_MONO8");
		e(0x1101, "AL_FORMAT_MONO16");
		e(0x1102, "AL_FORMAT_STEREO8");
		e(0x1103, "AL_FORMAT_STEREO16");

		/* Buffer properties */
		e(0x2001, "AL_FREQUENCY");
		e(0x2002, "AL_BITS");
		e(0x2003, "AL_CHANNELS");
		e(0x2004, "AL_SIZE");

		/* Listener */
		e(0x100F, "AL_ORIENTATION");

		/* Global state */
		e(0xB000, "AL_DOPPLER_FACTOR");
		e(0xB001, "AL_DOPPLER_VELOCITY");
		e(0xB003, "AL_SPEED_OF_SOUND");
		e(0xD000, "AL_DISTANCE_MODEL");
		e(0xD001, "AL_INVERSE_DISTANCE");
		e(0xD002, "AL_INVERSE_DISTANCE_CLAMPED");
		e(0xD003, "AL_LINEAR_DISTANCE");
		e(0xD004, "AL_LINEAR_DISTANCE_CLAMPED");
		e(0xD005, "AL_EXPONENT_DISTANCE");
		e(0xD006, "AL_EXPONENT_DISTANCE_CLAMPED");

		/* AL string queries */
		e(0xB001, "AL_VENDOR");
		e(0xB002, "AL_VERSION");
		e(0xB003, "AL_RENDERER");
		e(0xB004, "AL_EXTENSIONS");

		/* ALC properties */
		e(0x1007, "ALC_FREQUENCY");
		e(0x1008, "ALC_REFRESH");
		e(0x1009, "ALC_SYNC");
		e(0x1010, "ALC_MONO_SOURCES");
		e(0x1011, "ALC_STEREO_SOURCES");

		/* ALC string queries */
		e(0x0004, "ALC_DEFAULT_DEVICE_SPECIFIER");
		e(0x0005, "ALC_DEVICE_SPECIFIER");
		e(0x0006, "ALC_EXTENSIONS");
		e(0x0310, "ALC_MAJOR_VERSION");
		e(0x0311, "ALC_MINOR_VERSION");
		e(0x0312, "ALC_ATTRIBUTES_SIZE");
		e(0x0313, "ALC_ALL_ATTRIBUTES");

		/* ALC 1.1 */
		e(0x0311, "ALC_CAPTURE_DEVICE_SPECIFIER");
		e(0x0312, "ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER");
		e(0x0311, "ALC_CAPTURE_SAMPLES");

		/* EXTEfx - Effect types */
		e(0x0001, "AL_EFFECT_REVERB");
		e(0x0002, "AL_EFFECT_CHORUS");
		e(0x0003, "AL_EFFECT_DISTORTION");
		e(0x0004, "AL_EFFECT_ECHO");
		e(0x0005, "AL_EFFECT_FLANGER");
		e(0x0006, "AL_EFFECT_FREQUENCY_SHIFTER");
		e(0x0007, "AL_EFFECT_VOCAL_MORPHER");
		e(0x0008, "AL_EFFECT_PITCH_SHIFTER");
		e(0x0009, "AL_EFFECT_RING_MODULATOR");
		e(0x000A, "AL_EFFECT_AUTOWAH");
		e(0x000B, "AL_EFFECT_COMPRESSOR");
		e(0x000C, "AL_EFFECT_EQUALIZER");
		e(0x8000, "AL_EFFECT_EAXREVERB");

		/* EXTEfx - Filter types */
		e(0x0001, "AL_FILTER_LOWPASS");
		e(0x0002, "AL_FILTER_HIGHPASS");
		e(0x0003, "AL_FILTER_BANDPASS");

		/* EXTEfx - Source properties */
		e(0x20000, "AL_DIRECT_FILTER");
		e(0x20001, "AL_AUXILIARY_SEND_FILTER");
		e(0x20002, "AL_AIR_ABSORPTION_FACTOR");
		e(0x20003, "AL_ROOM_ROLLOFF_FACTOR");
		e(0x20004, "AL_CONE_OUTER_GAINHF");
		e(0x20005, "AL_DIRECT_FILTER_GAINHF_AUTO");
		e(0x20006, "AL_AUXILIARY_SEND_FILTER_GAIN_AUTO");
		e(0x20007, "AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO");

		/* EXTEfx - Effect slot */
		e(0x0001, "AL_EFFECTSLOT_EFFECT");
		e(0x0002, "AL_EFFECTSLOT_GAIN");
		e(0x0003, "AL_EFFECTSLOT_AUXILIARY_SEND_AUTO");
		e(0x0004, "AL_EFFECTSLOT_NULL");

		/* EXTEfx - Effect/Filter properties */
		e(0x8001, "AL_EFFECT_TYPE");
		e(0x8002, "AL_FILTER_TYPE");
	}

	private static void e(int value, String name) {
		AL_ENUMS.putIfAbsent(value, name);
	}

	public static String alErrorName(int error) {
		String name = AL_ERRORS.get(error);
		return name != null ? name : "Unknown AL error 0x" + Integer.toHexString(error);
	}

	public static String alcErrorName(int error) {
		String name = ALC_ERRORS.get(error);
		return name != null ? name : "Unknown ALC error 0x" + Integer.toHexString(error);
	}

	public static String enumName(int value) {
		String name = AL_ENUMS.get(value);
		return name != null ? name : "0x" + Integer.toHexString(value);
	}
}
