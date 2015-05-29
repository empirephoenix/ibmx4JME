package ibxm.jme;

import ibxm.Channel;
import ibxm.IBXM;
import ibxm.INoteListener;
import ibxm.Instrument;
import ibxm.Module;
import ibxm.WavInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jme3.asset.AssetInfo;
import com.jme3.audio.AudioBuffer;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioStream;

public class IBXMAdvancedLoader {
	private static final int	SAMPLE_RATE	= 48000, FADE_SECONDS = 16, REVERB_MILLIS = 50;
	private int					duration;
	private Module				module;
	private IBXM				ibxm;
	private AudioData			audiodata;

	public IBXMAdvancedLoader(final AssetInfo assetInfo, final INoteListener listener) throws IOException {
		if (assetInfo == null) {
			throw new RuntimeException("Supplied assetinfo invalid " + assetInfo);
		}
		assert assetInfo.getKey() instanceof AudioKey;
		final AudioKey audioKey = (AudioKey) assetInfo.getKey();

		// module loading
		final InputStream inputStream = assetInfo.openStream();
		try {
			this.module = new Module(inputStream);
			final IBXM ibxm = new IBXM(this.module, IBXMAdvancedLoader.SAMPLE_RATE);
			ibxm.setInterpolation(Channel.SINC);
			this.duration = ibxm.calculateSongDuration();
			final String songName = this.module.songName.trim();
			System.out.println("Loading " + songName);
			System.out.println("Song size estimated: " + (ibxm.calculateSongDuration() / IBXMAdvancedLoader.SAMPLE_RATE));
			final Instrument[] instruments = this.module.instruments;
			for (int idx = 0, len = instruments.length; idx < len; idx++) {
				final String name = instruments[idx].name;
				if (name.trim().length() > 0) {
					System.out.println("Instrument:" + String.format("%03d: %s", idx, name));
				}
			}
			this.ibxm = ibxm;
			this.ibxm.addNoteListener(listener);

		} finally {
			inputStream.close();
		}

		final WavInputStream ins = new WavInputStream(this.ibxm);

		if (audioKey.isStream()) {
			System.out.println("Streaming");
			final AudioStream as = new com.jme3.audio.AudioStream();
			as.setupFormat(2, 16, IBXMAdvancedLoader.SAMPLE_RATE);
			as.updateData(ins, -1);
			this.audiodata = as;
		} else {
			System.out.println("Buffered");
			final ByteArrayOutputStream boutCache = new ByteArrayOutputStream();
			boolean notFinish = true;
			final byte[] cache = new byte[1024 * 10];
			while (notFinish) {
				final int read = ins.read(cache);
				if (read == -1) {
					notFinish = false;
				} else {
					boutCache.write(cache, 0, read);
				}
			}
			final byte[] dataArray = boutCache.toByteArray();

			final ByteBuffer nativePCM = ByteBuffer.allocateDirect(dataArray.length);
			nativePCM.put(dataArray);
			final AudioBuffer as = new AudioBuffer();
			as.setupFormat(2, 16, IBXMAdvancedLoader.SAMPLE_RATE);
			as.updateData(nativePCM);
			this.audiodata = as;
		}
	}

	public AudioData getAudioData() {
		return this.audiodata;
	}
}
