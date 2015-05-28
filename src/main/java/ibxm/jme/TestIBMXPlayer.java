package ibxm.jme;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.AudioNode;
import com.jme3.system.AppSettings;

public class TestIBMXPlayer extends SimpleApplication {

	public static void main(String[] args) {
		final AppSettings settings = new AppSettings(true);
		// settings.setGammaCorrection(true);
		final TestIBMXPlayer t = new TestIBMXPlayer();
		t.setSettings(settings);
		t.start();
	}

	private AudioNode	anode;

	public TestIBMXPlayer() {
		this.setShowSettings(false);
	}

	@Override
	public void simpleInitApp() {
		this.flyCam.setDragToRotate(true);
		this.assetManager.registerLoader(IBXMLoader.class, "mod");
		this.assetManager.registerLocator("ibxm", ClasspathLocator.class);

		this.anode = new AudioNode(this.assetManager, "jme/a.mod", true);
		this.anode.setPositional(false);
		this.anode.play();
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
	}
}
