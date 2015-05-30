package ibxm.jme;

import ibxm.INoteListener;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class TestIBMXPlayer extends SimpleApplication {

	private static final float	FADE_TIME	= 0.9f;

	public static void main(final String[] args) throws SocketException {
		final AppSettings settings = new AppSettings(true);
		// settings.setGammaCorrection(true);
		final TestIBMXPlayer t = new TestIBMXPlayer();
		t.setSettings(settings);
		t.start();
	}

	private AudioNode								anode;
	private float									playtime	= -1;
	protected ConcurrentHashMap<Float, NoteInfo>	todispatch	= new ConcurrentHashMap<>();
	private Material								mat;
	final ColorRGBA									ncolor		= new ColorRGBA(0, 0, 0, 0);

	public TestIBMXPlayer() throws SocketException {
		this.setShowSettings(false);
	}

	@Override
	public void simpleInitApp() {
		this.setPauseOnLostFocus(false);

		this.flyCam.setDragToRotate(true);
		this.assetManager.registerLoader(IBXMLoader.class, "mod");
		this.assetManager.registerLocator("ibxm", ClasspathLocator.class);

		final AudioKey ak = new AudioKey("jme/a.mod", true);
		final AssetInfo loadInfo = this.assetManager.locateAsset(ak);
		try {
			final IBXMAdvancedLoader al = new IBXMAdvancedLoader(loadInfo, new INoteListener() {

				@Override
				public void onNote(final float posInSec, final int note, final int volume, final int noteKey, final int fadeoutVol, final int instrumentId, final int panning) {
					TestIBMXPlayer.this.todispatch.put(posInSec, new NoteInfo(note, volume, noteKey, fadeoutVol, instrumentId, panning));
				}
			});
			this.anode = new AudioNode(al.getAudioData(), ak);
			this.anode.setPositional(false);
			this.anode.play();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final Geometry geom = new Geometry("Box", new Box(0.5f, 0.5f, 0.5f));
		this.mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		geom.setMaterial(this.mat);
		this.rootNode.attachChild(geom);

	}

	@Override
	public void simpleUpdate(final float tpf) {
		this.playtime += tpf;
		boolean fb = true, fg = true, fr = true;
		final List<Float> removeKeys = new ArrayList<>();
		for (final Entry<Float, NoteInfo> ee : this.todispatch.entrySet()) {
			if (ee.getKey() < this.playtime) {
				final NoteInfo note = ee.getValue();
				removeKeys.add(ee.getKey());
				final int channel = note.note % 3;
				if (channel == 0) {
					this.ncolor.b = note.volume / 64f * note.globalVolume / 64f;
					fb = false;
				}
				if (channel == 1) {
					this.ncolor.g = note.volume / 64f * note.globalVolume / 64f;
					fg = false;
				}
				if (channel == 2) {
					this.ncolor.r = note.volume / 64f * note.globalVolume / 64f;
					fr = false;
				}
				System.out.println(note.note + " " + note.noteKey + " " + note.globalVolume);
			}
		}
		for (final Float r : removeKeys) {
			this.todispatch.remove(r);
		}
		if (fr) {
			this.ncolor.r = this.ncolor.r * TestIBMXPlayer.FADE_TIME;
		}
		if (fg) {
			this.ncolor.g = this.ncolor.g * TestIBMXPlayer.FADE_TIME;
		}
		if (fb) {
			this.ncolor.b = this.ncolor.b * TestIBMXPlayer.FADE_TIME;
		}
		System.out.println(this.ncolor);

		this.mat.setColor("Color", this.ncolor);
	}
}
