package ibxm.jme;

import ibxm.Channel;
import ibxm.IBXM;
import ibxm.Instrument;
import ibxm.Module;
import ibxm.WavInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.audio.AudioBuffer;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioStream;

public class IBXMLoader implements AssetLoader {
	private static final int	SAMPLE_RATE	= 48000, FADE_SECONDS = 16, REVERB_MILLIS = 50;
	private int					duration;
	private Module				module;
	private IBXM				ibxm;

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		assert assetInfo.getKey() instanceof AudioKey;
		AudioKey audioKey = (AudioKey) assetInfo.getKey();

		// module loading
		InputStream inputStream = assetInfo.openStream();
		try {
			this.module = new Module(inputStream);
			IBXM ibxm = new IBXM(this.module, IBXMLoader.SAMPLE_RATE);
			ibxm.setInterpolation(Channel.SINC);
			this.duration = ibxm.calculateSongDuration();
			String songName = this.module.songName.trim();
			System.out.println("Loading " + songName);
			Instrument[] instruments = this.module.instruments;
			for (int idx = 0, len = instruments.length; idx < len; idx++) {
				String name = instruments[idx].name;
				if (name.trim().length() > 0)
					System.out.println("Instrument:" + String.format("%03d: %s", idx, name));
			}
			this.ibxm = ibxm;
		} finally {
			inputStream.close();
		}

		WavInputStream ins = new WavInputStream(this.ibxm);

		if (audioKey.isStream()) {
			System.out.println("Streaming");
			AudioStream as = new com.jme3.audio.AudioStream();
			as.setupFormat(2, 16, IBXMLoader.SAMPLE_RATE);
			as.updateData(ins, -1);
			return as;
		} else {
			System.out.println("Buffered");
			ByteArrayOutputStream boutCache = new ByteArrayOutputStream();
			boolean notFinish = true;
			byte[] cache = new byte[1024 * 10];
			while (notFinish) {
				int read = ins.read(cache);
				if (read == -1) {
					notFinish = false;
				} else {
					boutCache.write(cache, 0, read);
				}
			}
			byte[] dataArray = boutCache.toByteArray();

			ByteBuffer nativePCM = ByteBuffer.allocateDirect(dataArray.length);
			nativePCM.put(dataArray);
			AudioBuffer as = new AudioBuffer();
			as.setupFormat(2, 16, IBXMLoader.SAMPLE_RATE);
			as.updateData(nativePCM);
			return as;
		}
		// return player.getAudioData();
	}
}
