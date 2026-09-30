package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC10.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EXTEfx;
import org.lwjglx.debug.Properties;

public class DebugALIT {

	private long device;
	private long context;

	@BeforeEach
	public void setUp() {
		device = alcOpenDevice((ByteBuffer) null);
		assumeTrue(device != 0L, "OpenAL device not available");
		context = alcCreateContext(device, (IntBuffer) null);
		assumeTrue(context != 0L, "OpenAL context creation failed");
		alcMakeContextCurrent(context);
		ALCCapabilities alcCaps = ALC.createCapabilities(device);
		AL.createCapabilities(alcCaps);
	}

	@AfterEach
	public void tearDown() {
		if (context != 0L) {
			alcMakeContextCurrent(0L);
			alcDestroyContext(context);
			context = 0L;
		}
		if (device != 0L) {
			alcCloseDevice(device);
			device = 0L;
		}
	}

	@Test
	public void testNoContextCurrent() {
		alcMakeContextCurrent(0L);
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alGenBuffers());
		assertTrue(ex.getMessage().contains("No OpenAL context is current"));
	}

	@Test
	public void testBufferLifecycleAndDoubleFree() {
		int buf = alGenBuffers();
		assertTrue(buf > 0);
		assertTrue(alIsBuffer(buf));

		alDeleteBuffers(buf);

		// Double-free
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alDeleteBuffers(buf));
		assertTrue(ex.getMessage().contains("already deleted (double-free)"));

		// Use-after-free
		ByteBuffer data = BufferUtils.createByteBuffer(16);
		IllegalStateException ex2 = assertThrows(IllegalStateException.class,
				() -> alBufferData(buf, AL_FORMAT_MONO16, data, 44100));
		assertTrue(ex2.getMessage().contains("has been deleted (use-after-free)"));
	}

	@Test
	public void testSourceLifecycleAndDoubleFree() {
		int src = alGenSources();
		assertTrue(src > 0);
		assertTrue(alIsSource(src));

		alDeleteSources(src);

		// Double-free
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alDeleteSources(src));
		assertTrue(ex.getMessage().contains("already deleted (double-free)"));

		// Use-after-free
		IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> alSourcePlay(src));
		assertTrue(ex2.getMessage().contains("has been deleted (use-after-free)"));
	}

	@Test
	public void testBufferAttachedToSourceDeleteWarning() {
		int buf = alGenBuffers();
		int src = alGenSources();

		alSourcei(src, AL_BUFFER, buf);

		// Deleting buffer while still attached
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alDeleteBuffers(buf));
		assertTrue(ex.getMessage().contains("is still attached to 1 source(s)"));

		// Detach buffer
		alSourcei(src, AL_BUFFER, 0);

		// Now deletion should succeed
		alDeleteBuffers(buf);

		// Clean up source
		alDeleteSources(src);
	}

	@Test
	public void testBufferDataOnAttachedBuffer() {
		int buf = alGenBuffers();
		int src = alGenSources();

		alSourcei(src, AL_BUFFER, buf);

		ByteBuffer data = BufferUtils.createByteBuffer(16);
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> alBufferData(buf, AL_FORMAT_MONO16, data, 44100));
		assertTrue(ex.getMessage().contains("while attached to source(s) is undefined behavior"));

		alSourcei(src, AL_BUFFER, 0);
		alDeleteBuffers(buf);
		alDeleteSources(src);
	}

	@Test
	public void testContextDestructionDoubleFree() {
		long tempCtx = alcCreateContext(device, (IntBuffer) null);
		assertTrue(tempCtx != 0L);

		alcDestroyContext(tempCtx);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alcDestroyContext(tempCtx));
		assertTrue(ex.getMessage().contains("already been destroyed (double-free)"));
	}

	@Test
	public void testMakeDestroyedContextCurrent() {
		long tempCtx = alcCreateContext(device, (IntBuffer) null);
		assertTrue(tempCtx != 0L);

		alcDestroyContext(tempCtx);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alcMakeContextCurrent(tempCtx));
		assertTrue(ex.getMessage().contains("has already been destroyed (use-after-free)"));
	}

	@Test
	public void testContextLeakedResourcesAudit() {
		long tempCtx = alcCreateContext(device, (IntBuffer) null);
		assertTrue(tempCtx != 0L);
		alcMakeContextCurrent(tempCtx);

		// Leak a source
		alGenSources();

		// Default: warn only
		alcDestroyContext(tempCtx);

		// With FAIL_ON_LEAKS: throws IllegalStateException
		long tempCtx2 = alcCreateContext(device, (IntBuffer) null);
		assertTrue(tempCtx2 != 0L);
		alcMakeContextCurrent(tempCtx2);
		alGenSources();

		Properties.FAIL_ON_LEAKS.enable();
		try {
			IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alcDestroyContext(tempCtx2));
			assertTrue(ex.getMessage().contains("leaked"));
		} finally {
			Properties.FAIL_ON_LEAKS.enabled = false;
		}
	}

	@Test
	public void testCloseDeviceWithLiveContext() {
		long tempDev = alcOpenDevice((ByteBuffer) null);
		assumeTrue(tempDev != 0L);
		long tempCtx = alcCreateContext(tempDev, (IntBuffer) null);
		assumeTrue(tempCtx != 0L);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alcCloseDevice(tempDev));
		assertTrue(ex.getMessage().contains("is still alive"));

		alcDestroyContext(tempCtx);
		alcCloseDevice(tempDev);
	}

	@Test
	public void testDeviceUseAfterFree() {
		long tempDev = alcOpenDevice((ByteBuffer) null);
		assumeTrue(tempDev != 0L);
		alcCloseDevice(tempDev);

		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> alcCreateContext(tempDev, (IntBuffer) null));
		assertTrue(ex.getMessage().contains("device has been closed"));
	}

	@Test
	public void testEXTEfxValidation() {
		ALCapabilities caps = AL.getCapabilities();
		assumeTrue(caps.ALC_EXT_EFX, "ALC_EXT_EFX extension not supported");

		int effect = EXTEfx.alGenEffects();
		assertTrue(effect > 0);
		assertTrue(EXTEfx.alIsEffect(effect));

		EXTEfx.alDeleteEffects(effect);

		// Double-free
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> EXTEfx.alDeleteEffects(effect));
		assertTrue(ex.getMessage().contains("already deleted (double-free)"));

		// Use-after-free
		IllegalStateException ex2 = assertThrows(IllegalStateException.class,
				() -> EXTEfx.alEffecti(effect, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_REVERB));
		assertTrue(ex2.getMessage().contains("has been deleted (use-after-free)"));

		// Filter
		int filter = EXTEfx.alGenFilters();
		assertTrue(filter > 0);
		EXTEfx.alDeleteFilters(filter);

		IllegalStateException ex3 = assertThrows(IllegalStateException.class, () -> EXTEfx.alDeleteFilters(filter));
		assertTrue(ex3.getMessage().contains("already deleted (double-free)"));

		// Aux Slot
		int slot = EXTEfx.alGenAuxiliaryEffectSlots();
		assertTrue(slot > 0);
		EXTEfx.alDeleteAuxiliaryEffectSlots(slot);

		IllegalStateException ex4 = assertThrows(IllegalStateException.class,
				() -> EXTEfx.alDeleteAuxiliaryEffectSlots(slot));
		assertTrue(ex4.getMessage().contains("already deleted (double-free)"));
	}

	@Test
	public void testCrossContextObjectUsage() {
		long ctx2 = alcCreateContext(device, (IntBuffer) null);
		assertTrue(ctx2 != 0L);

		int buf = alGenBuffers();
		assertTrue(buf > 0);
		int src = alGenSources();
		assertTrue(src > 0);

		alcMakeContextCurrent(ctx2);
		ByteBuffer data = BufferUtils.createByteBuffer(16);
		alBufferData(buf, AL_FORMAT_MONO16, data, 44100);

		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> alSourcei(src, AL_BUFFER, buf));
		assertTrue(ex.getMessage().contains("cross-context use"));

		long dev2 = alcOpenDevice((ByteBuffer) null);
		if (dev2 != 0L) {
			try {
				long ctx3 = alcCreateContext(dev2, (IntBuffer) null);
				if (ctx3 != 0L) {
					try {
						alcMakeContextCurrent(ctx3);
						IllegalStateException ex2 = assertThrows(IllegalStateException.class,
								() -> alBufferData(buf, AL_FORMAT_MONO16, data, 44100));
						assertTrue(ex2.getMessage().contains("cross-device use"));
					} finally {
						alcDestroyContext(ctx3);
					}
				}
			} finally {
				alcCloseDevice(dev2);
			}
		}

		alcMakeContextCurrent(context);
		alDeleteSources(src);
		alDeleteBuffers(buf);
		alcDestroyContext(ctx2);
	}

	@Test
	public void testDeviceBufferLeakAudit() {
		long tempDev = alcOpenDevice((ByteBuffer) null);
		assumeTrue(tempDev != 0L);
		long tempCtx = alcCreateContext(tempDev, (IntBuffer) null);
		assumeTrue(tempCtx != 0L);
		alcMakeContextCurrent(tempCtx);

		int buf = alGenBuffers();
		assertTrue(buf > 0);

		alcDestroyContext(tempCtx);

		Properties.FAIL_ON_LEAKS.enable();
		try {
			IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alcCloseDevice(tempDev));
			assertTrue(ex.getMessage().contains("leaked"));
		} finally {
			Properties.FAIL_ON_LEAKS.enabled = false;
			alcCloseDevice(tempDev);
			alcMakeContextCurrent(context);
		}
	}

	@Test
	public void testMultiQueuedBuffers() {
		int src = alGenSources();
		int buf = alGenBuffers();
		ByteBuffer data = BufferUtils.createByteBuffer(16);
		alBufferData(buf, AL_FORMAT_MONO16, data, 44100);

		alSourceQueueBuffers(src, buf);
		alSourceQueueBuffers(src, buf);

		alSourcePlay(src);
		alSourceStop(src);

		int unqueued = alSourceUnqueueBuffers(src);
		assertEquals(buf, unqueued);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> alDeleteBuffers(buf));
		assertTrue(ex.getMessage().contains("still attached"));

		unqueued = alSourceUnqueueBuffers(src);
		assertEquals(buf, unqueued);

		alDeleteBuffers(buf);
		alDeleteSources(src);
	}

	@Test
	public void testEfxFilterValidation() {
		ALCapabilities caps = AL.getCapabilities();
		assumeTrue(caps.ALC_EXT_EFX, "ALC_EXT_EFX extension not supported");

		int filter = EXTEfx.alGenFilters();
		EXTEfx.alDeleteFilters(filter);

		int src = alGenSources();
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> alSourcei(src, EXTEfx.AL_DIRECT_FILTER, filter));
		assertTrue(ex.getMessage().contains("use-after-free"));

		int slot = EXTEfx.alGenAuxiliaryEffectSlots();
		IllegalStateException ex2 = assertThrows(IllegalStateException.class,
				() -> alSource3i(src, EXTEfx.AL_AUXILIARY_SEND_FILTER, slot, 0, filter));
		assertTrue(ex2.getMessage().contains("use-after-free"));

		EXTEfx.alDeleteAuxiliaryEffectSlots(slot);
		alDeleteSources(src);
	}

	@Test
	public void testAlcDestroyWithOpenDevice() {
		long tempDev = alcOpenDevice((ByteBuffer) null);
		assumeTrue(tempDev != 0L);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ALC.destroy());
		assertTrue(ex.getMessage().contains("was never closed"));

		alcCloseDevice(tempDev);
	}

	@Test
	public void testTraceWithOpenAL() {
		org.lwjglx.debug.Properties.TRACE.enable();
		try {
			int buf = alGenBuffers();
			assertTrue(buf > 0);
			ByteBuffer data = BufferUtils.createByteBuffer(16);
			alBufferData(buf, AL_FORMAT_MONO16, data, 44100);
			int freq = alGetBufferi(buf, AL_FREQUENCY);
			assertEquals(44100, freq);
			alDeleteBuffers(buf);
		} finally {
			org.lwjglx.debug.Properties.TRACE.enabled = false;
		}
	}

	@Test
	public void testAlMetadataEnumNaming() {
		assertEquals("AL_NO_ERROR", org.lwjglx.debug.openal.ALMetadata.enumName(0));
		assertEquals("AL_NO_ERROR", org.lwjglx.debug.openal.ALMetadata.alErrorName(0));
		assertEquals("AL_INVALID_NAME", org.lwjglx.debug.openal.ALMetadata.enumName(0xA001));
		assertEquals("AL_INVALID_NAME", org.lwjglx.debug.openal.ALMetadata.alErrorName(0xA001));
	}

	@Test
	public void testValidateToggleSuppressesAlError() {
		Properties.VALIDATE.enabled = false;
		try {
			org.lwjglx.debug.openal.ALRT.checkALError("testMethod");
			org.lwjglx.debug.openal.ALRT.checkALCError(device, "testMethod");
		} finally {
			Properties.VALIDATE.enabled = true;
		}
	}
}
