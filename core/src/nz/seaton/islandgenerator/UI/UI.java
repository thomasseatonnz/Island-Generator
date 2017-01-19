package nz.seaton.islandgenerator.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.widget.VisTextField;

public class UI {
	int w, h;

	public final boolean _DEBUG = false;
	// DEBUG
	int count;

	// Base Containers
	Stage stage;
	VisTable islandOptionsTable;
	VisTable simplexOptionsTable;
	VisTable centerConsoleTable;
	VisTable loadingBarTable;
	VisTable FPSBox;

	// FPS Widget
	VisLabel FPSLabel;

	// Loading Widgets
	VisLabel currentLoadLabel;
	VisProgressBar progress;
	VisLabel loadStateLabel;

	// Other Loading Objects
	public boolean loading = false;
	float loadProgress = 0.0f;

	// TODO: sort this out
	VisLabel islandNameLabel;

	public UI(int _w, int _h) {
		w = _w;
		h = _h;

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		VisUI.load();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/Roboto-Regular.ttf"));

		// *** Island Options *** //
		islandOptionsTable = new VisTable();
		islandOptionsTable.setFillParent(true);
		islandOptionsTable.setDebug(_DEBUG);

		createIslandOptionsUI(generator);

		islandOptionsTable.bottom().left().pad(10);
		stage.addActor(islandOptionsTable);

		// *** Simplex Options *** //
		simplexOptionsTable = new VisTable();
		simplexOptionsTable.setFillParent(true);
		simplexOptionsTable.setDebug(_DEBUG);

		CreateSimplexOptionsUI(generator);

		simplexOptionsTable.bottom().right().pad(10);
		stage.addActor(simplexOptionsTable);

		// *** Center Console *** //
		centerConsoleTable = new VisTable(true);
		centerConsoleTable.setFillParent(true);
		centerConsoleTable.setDebug(_DEBUG);

		generateCenterConsole(generator, centerConsoleTable);

		centerConsoleTable.bottom().pad(10);
		stage.addActor(centerConsoleTable);

		// *** Center Console *** //
		loadingBarTable = new VisTable();
		loadingBarTable.setFillParent(true);
		loadingBarTable.setDebug(_DEBUG);

		generateLoadingBar(generator, loadingBarTable);

		stage.addActor(loadingBarTable);
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				loading = true;
				loadStateLabel.setText("Loading 'clicking'");
			}
		});

		// *** FPS Counter *** //
		FPSBox = new VisTable();
		FPSBox.setFillParent(true);
		FPSBox.setDebug(_DEBUG);

		generateFPSBox(generator, FPSBox);

		FPSBox.top().left().pad(10);
		stage.addActor(FPSBox);

		// *** Clean Up *** //
		generator.dispose();
	}

	private void generateFPSBox(FreeTypeFontGenerator generator, VisTable table) {
		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 35;
		config.borderWidth = 1;
		config.borderColor = Color.WHITE;
		config.color = Color.BLACK;

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = generator.generateFont(config);

		FPSLabel = new VisLabel("0", labelStyle);
		FPSLabel.setAlignment(Align.top | Align.left);
		table.add(FPSLabel);
	}

	private void generateLoadingBar(FreeTypeFontGenerator generator, VisTable table) {
		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 25;

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = generator.generateFont(config);
		labelStyle.fontColor = Color.WHITE;
		labelStyle.background = VisUI.getSkin().getDrawable("button");

		// "Loading..." label
		currentLoadLabel = new VisLabel("Loading...", labelStyle);
		currentLoadLabel.setAlignment(Align.center | Align.top);
		table.add(currentLoadLabel).fill().width(400).height(30);
		table.row();

		// Loading Bar
		progress = new VisProgressBar(0.0f, 100f, 0.1f, false);
		table.add(progress).fill();
		table.row();

		// current load state
		config.size = 15;
		labelStyle.font = generator.generateFont(config);

		loadStateLabel = new VisLabel("Creating Perlin Noise", labelStyle);
		loadStateLabel.setAlignment(Align.center);
		table.add(loadStateLabel).fill().height(30);

	}

	private void generateCenterConsole(FreeTypeFontGenerator generator, VisTable table) {
		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 25;

		VisTextButtonStyle genButtonStyle = new VisTextButtonStyle(VisUI.getSkin().getDrawable("button"), VisUI.getSkin().getDrawable("button-down"), VisUI.getSkin().getDrawable("button-blue-down"), generator.generateFont(config));
		genButtonStyle.over = VisUI.getSkin().getDrawable("button-over");
		final VisTextButton generateButton = new VisTextButton("Generate", genButtonStyle);

		generateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generateButton.setChecked(false);
			}
		});

		table.add(generateButton).width(220);

		VisTextButtonStyle debugButtonStyle = new VisTextButtonStyle(VisUI.getSkin().getDrawable("button"), VisUI.getSkin().getDrawable("button-down"), VisUI.getSkin().getDrawable("button-blue-down"), generator.generateFont(config));
		debugButtonStyle.over = VisUI.getSkin().getDrawable("button-over");
		VisTextButton debugButton = new VisTextButton("Debug Disabled", debugButtonStyle);

		table.add(debugButton).width(220);
	}

	private void createIslandOptionsUI(FreeTypeFontGenerator generator) {
		// ISLAND NAME
		LabelStyle islandNameStyle = new LabelStyle();

		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 30;

		islandNameStyle.font = generator.generateFont(config);
		islandNameStyle.fontColor = Color.WHITE;
		islandNameStyle.background = VisUI.getSkin().getDrawable("button");

		islandNameLabel = new VisLabel("Island Name", islandNameStyle);
		islandNameLabel.setAlignment(Align.center);
		islandOptionsTable.add(islandNameLabel).fill();

		VisTable optionsTable = new VisTable();
		optionsTable.setDebug(_DEBUG);

		// SEED
		LabelStyle optionLabelStyle = new LabelStyle();
		config.size = 15;
		optionLabelStyle.font = generator.generateFont(config);
		optionLabelStyle.fontColor = Color.WHITE;
		optionLabelStyle.background = VisUI.getSkin().getDrawable("button");

		VisLabel seedLabel = new VisLabel("seed: ", optionLabelStyle);
		seedLabel.setAlignment(Align.right);
		VisTextField seedText = new VisTextField("seed");
		seedText.setAlignment(Align.center);

		optionsTable.add(seedLabel).fill();
		optionsTable.add(seedText);
		optionsTable.row();

		// Beach Size
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = VisUI.getSkin().getDrawable("window-noborder");
		sliderStyle.knob = VisUI.getSkin().getDrawable("slider-knob");
		sliderStyle.knobDown = VisUI.getSkin().getDrawable("slider-knob-down");
		sliderStyle.knobOver = VisUI.getSkin().getDrawable("slider-knob-over");
		sliderStyle.disabledKnob = VisUI.getSkin().getDrawable("slider-knob-disabled");

		VisLabel beachSizeLabel = new VisLabel("beach size: ", optionLabelStyle);
		beachSizeLabel.setAlignment(Align.right);
		VisSlider beachSizeSlider = new VisSlider(0.0f, 1.0f, 0.01f, false, sliderStyle);
		beachSizeSlider.setValue(0.035f);

		optionsTable.row();
		optionsTable.add(beachSizeLabel).fill();
		optionsTable.add(beachSizeSlider).fill();

		// waterLevel
		VisLabel waterLevelLabel = new VisLabel("water level: ", optionLabelStyle);
		waterLevelLabel.setAlignment(Align.right);
		VisSlider waterLevelSlider = new VisSlider(-0.5f, 1.0f, 0.01f, false, sliderStyle);
		waterLevelSlider.setValue(0.05f);

		optionsTable.row();
		optionsTable.add(waterLevelLabel).fill();
		optionsTable.add(waterLevelSlider).fill();

		// NOT AVAILABLE IN THIS BRANCH
		// //tides
		// VisLabel tidesOptionLabel = new VisLabel("tides: ", optionLabelStyle);
		// tidesOptionLabel.setAlignment(Align.right);
		// VisTextButtonStyle buttonStyle = new VisTextButtonStyle(VisUI.getSkin().getDrawable("button"), VisUI.getSkin().getDrawable("button-down"),
		// VisUI.getSkin().getDrawable("button-blue-down"), generator.generateFont(config));
		// buttonStyle.over = VisUI.getSkin().getDrawable("button-over");
		// VisTextButton tidesOption = new VisTextButton("Enabled", buttonStyle);
		// tidesOption.setChecked(true); //Start enabled
		//
		// optionsTable.row();
		// optionsTable.add(tidesOptionLabel).fill();
		// optionsTable.add(tidesOption).fill();

		// d/x
		VisLabel overxLabel = new VisLabel("d/x: ", optionLabelStyle);
		overxLabel.setAlignment(Align.right);
		VisSlider overxSlider = new VisSlider(-5, 20f, 0.5f, false, sliderStyle);
		overxSlider.setValue(10f);

		optionsTable.row();
		optionsTable.add(overxLabel).fill();
		optionsTable.add(overxSlider).fill();

		// +x
		VisLabel plusxLabel = new VisLabel("+x: ", optionLabelStyle);
		plusxLabel.setAlignment(Align.right);
		VisSlider plusxSlider = new VisSlider(0, 10f, 0.01f, false, sliderStyle);
		plusxSlider.setValue(1.5f);

		optionsTable.row();
		optionsTable.add(plusxLabel).fill();
		optionsTable.add(plusxSlider).fill();

		// Finish it all up
		islandOptionsTable.row();
		islandOptionsTable.add(optionsTable);

	}

	private void CreateSimplexOptionsUI(FreeTypeFontGenerator generator) {
		LabelStyle simplexNameStyle = new LabelStyle();

		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 30;

		simplexNameStyle.font = generator.generateFont(config);
		simplexNameStyle.fontColor = Color.WHITE;
		simplexNameStyle.background = VisUI.getSkin().getDrawable("button");

		final VisLabel simplexLabel = new VisLabel("Simplex Noise", simplexNameStyle);
		simplexOptionsTable.add(simplexLabel).fill();

		VisTable optionsTable = new VisTable();
		optionsTable.setDebug(_DEBUG);

		LabelStyle optionLabelStyle = new LabelStyle();
		config.size = 15;
		optionLabelStyle.font = generator.generateFont(config);
		optionLabelStyle.fontColor = Color.WHITE;
		optionLabelStyle.background = VisUI.getSkin().getDrawable("button");

		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = VisUI.getSkin().getDrawable("window-noborder");
		sliderStyle.knob = VisUI.getSkin().getDrawable("slider-knob");
		sliderStyle.knobDown = VisUI.getSkin().getDrawable("slider-knob-down");
		sliderStyle.knobOver = VisUI.getSkin().getDrawable("slider-knob-over");
		sliderStyle.disabledKnob = VisUI.getSkin().getDrawable("slider-knob-disabled");

		// Octaves

		VisLabel octLabel = new VisLabel("Octaves: ", optionLabelStyle);
		octLabel.setAlignment(Align.right);
		VisSlider octSlider = new VisSlider(1, 50f, 1f, false, sliderStyle);
		octSlider.setValue(10f);

		optionsTable.row();
		optionsTable.add(octLabel).fill();
		optionsTable.add(octSlider).fill();

		// freq

		VisLabel freqLabel = new VisLabel("Frequency: ", optionLabelStyle);
		freqLabel.setAlignment(Align.right);
		VisSlider freqSlider = new VisSlider(0, 0.1f, 0.00001f, false, sliderStyle);
		freqSlider.setValue(0.0005f);

		optionsTable.row();
		optionsTable.add(freqLabel).fill();
		optionsTable.add(freqSlider).fill();

		// Amplitude

		VisLabel ampLabel = new VisLabel("Amplitude: ", optionLabelStyle);
		ampLabel.setAlignment(Align.right);
		VisSlider ampSlider = new VisSlider(0f, 2000f, 1f, false, sliderStyle);
		ampSlider.setValue(800f);

		optionsTable.row();
		optionsTable.add(ampLabel).fill();
		optionsTable.add(ampSlider).fill();

		// Persistance

		VisLabel persLabel = new VisLabel("Persistance: ", optionLabelStyle);
		persLabel.setAlignment(Align.right);
		VisSlider persSlider = new VisSlider(0, 1f, 0.01f, false, sliderStyle);
		persSlider.setValue(0.7f);

		optionsTable.row();
		optionsTable.add(persLabel).fill();
		optionsTable.add(persSlider).fill();

		// Finish it all up

		simplexOptionsTable.row();
		simplexOptionsTable.add(optionsTable);
	}

	public void render() {
		stage.draw();
	}

	long last = System.currentTimeMillis();

	public void update() {
		FPSLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));

		// Debug
		if (loading) {
			count++;
			loadProgress = (float) count / 5;
			progress.setValue(loadProgress);

			long ti = System.currentTimeMillis() - last; //time interval
			if (ti >= 1000 && ti < 2000) {
				currentLoadLabel.setText("Loading.  ");
			} else if (ti >= 2000 && ti < 3000) {
				currentLoadLabel.setText("Loading.. ");
			} else if (ti >= 3000) {
				currentLoadLabel.setText("Loading...");
				last = System.currentTimeMillis();
			}

			if (loadProgress >= 100) {
				loading = false;
				loadProgress = 0;
				count = 0;
			} else if (loadProgress > 80)
				loadStateLabel.setText("Making a meme");
			else if (loadProgress > 70)
				loadStateLabel.setText("eating carrots");
			else if (loadProgress > 60)
				loadStateLabel.setText("Taking note");
			else if (loadProgress > 40)
				loadStateLabel.setText("count++");
			else if (loadProgress > 20)
				loadStateLabel.setText("taking pictures of cute dogs");
		}
		// --end Debug

		loadingBarTable.setVisible(loading);
		simplexOptionsTable.setVisible(!loading);
		centerConsoleTable.setVisible(!loading);
		islandOptionsTable.setVisible(!loading);

		stage.act(Gdx.graphics.getDeltaTime());
	}

	public void dispose() {
		stage.dispose();
		VisUI.dispose();
	}
}
