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

import nz.seaton.islandgenerator.IslandGenerator;
import nz.seaton.islandgenerator.island.Island;

import com.kotcrab.vis.ui.widget.VisTextField;

public class UI {
	int w, h;
	Island island;

	public void setIsland(Island i) {
		island = i;
	}

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

	// Island Setting Widgets
	VisTextField seedText;
	VisSlider beachSizeSlider;
	VisSlider waterLevelSlider;
	VisSlider overxSlider;
	VisSlider plusxSlider;
	VisSlider peakSlider;
	VisLabel islandNameLabel;

	// Simplex Settings Widgets
	VisSlider octSlider;
	VisSlider freqSlider;
	VisSlider ampSlider;
	VisSlider persSlider;

	// FPS Widget
	VisLabel FPSLabel;

	// Loading Widgets
	VisLabel loadingTitleLabel;
	VisProgressBar progress;
	VisLabel loadStateLabel;

	// Other Loading Objects
	public boolean loading = false;
	float loadProgress = 0.0f;

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
		loadingTitleLabel = new VisLabel("Loading...", labelStyle);
		loadingTitleLabel.setAlignment(Align.center | Align.top);
		table.add(loadingTitleLabel).fill().width(400).height(30);
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
				generateIslandWithSettings();
			}
		});

		table.add(generateButton).width(220);

		VisTextButtonStyle debugButtonStyle = new VisTextButtonStyle(VisUI.getSkin().getDrawable("button"), VisUI.getSkin().getDrawable("button-down"), VisUI.getSkin().getDrawable("button-blue-down"), generator.generateFont(config));
		debugButtonStyle.over = VisUI.getSkin().getDrawable("button-over");
		final VisTextButton debugButton = new VisTextButton("Debug Disabled", debugButtonStyle);
		debugButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				IslandGenerator.DEBUG = debugButton.isChecked();
			}
		});

		table.add(debugButton).width(220);
	}

	private void createIslandOptionsUI(FreeTypeFontGenerator generator) {
		// ISLAND NAME
		LabelStyle islandNameStyle = new LabelStyle();

		FreeTypeFontParameter config = new FreeTypeFontParameter();
		config.size = 25;

		islandNameStyle.font = generator.generateFont(config);
		islandNameStyle.fontColor = Color.WHITE;
		islandNameStyle.background = VisUI.getSkin().getDrawable("button");

		islandNameLabel = new VisLabel("Island Name", islandNameStyle);
		islandNameLabel.setAlignment(Align.center);
		islandNameLabel.setWrap(true);
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
		seedText = new VisTextField("seed");
		seedText.setAlignment(Align.center);

		optionsTable.add(seedLabel).fill();
		optionsTable.add(seedText);
		optionsTable.row();
		
		//Peaks
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = VisUI.getSkin().getDrawable("window-noborder");
		sliderStyle.knob = VisUI.getSkin().getDrawable("slider-knob");
		sliderStyle.knobDown = VisUI.getSkin().getDrawable("slider-knob-down");
		sliderStyle.knobOver = VisUI.getSkin().getDrawable("slider-knob-over");
		sliderStyle.disabledKnob = VisUI.getSkin().getDrawable("slider-knob-disabled");
		
		VisLabel peaksLabel = new VisLabel("peaks: ", optionLabelStyle);
		peaksLabel.setAlignment(Align.right);
		peakSlider = new VisSlider(0.0f, 6f, 1.0f, false, sliderStyle);
		peakSlider.setValue(1f);
		
		optionsTable.row();
		optionsTable.add(peaksLabel).fill();
		optionsTable.add(peakSlider).fill();

		// Beach Size
		VisLabel beachSizeLabel = new VisLabel("beach size: ", optionLabelStyle);
		beachSizeLabel.setAlignment(Align.right);
		beachSizeSlider = new VisSlider(0.0f, 1.0f, 0.01f, false, sliderStyle);
		beachSizeSlider.setValue(0.035f);

		optionsTable.row();
		optionsTable.add(beachSizeLabel).fill();
		optionsTable.add(beachSizeSlider).fill();

		// waterLevel
		VisLabel waterLevelLabel = new VisLabel("water level: ", optionLabelStyle);
		waterLevelLabel.setAlignment(Align.right);
		waterLevelSlider = new VisSlider(-0.5f, 1.0f, 0.01f, false, sliderStyle);
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
		overxSlider = new VisSlider(-5, 20f, 0.5f, false, sliderStyle);
		overxSlider.setValue(10f);

		optionsTable.row();
		optionsTable.add(overxLabel).fill();
		optionsTable.add(overxSlider).fill();

		// +x
		VisLabel plusxLabel = new VisLabel("+x: ", optionLabelStyle);
		plusxLabel.setAlignment(Align.right);
		plusxSlider = new VisSlider(0, 10f, 0.01f, false, sliderStyle);
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
		octSlider = new VisSlider(1, 50f, 1f, false, sliderStyle);
		octSlider.setValue(10f);

		optionsTable.row();
		optionsTable.add(octLabel).fill();
		optionsTable.add(octSlider).fill();

		// freq

		VisLabel freqLabel = new VisLabel("Frequency: ", optionLabelStyle);
		freqLabel.setAlignment(Align.right);
		freqSlider = new VisSlider(0, 0.1f, 0.00001f, false, sliderStyle);
		freqSlider.setValue(0.0005f);

		optionsTable.row();
		optionsTable.add(freqLabel).fill();
		optionsTable.add(freqSlider).fill();

		// Amplitude

		VisLabel ampLabel = new VisLabel("Amplitude: ", optionLabelStyle);
		ampLabel.setAlignment(Align.right);
		ampSlider = new VisSlider(0f, 2000f, 1f, false, sliderStyle);
		ampSlider.setValue(800f);

		optionsTable.row();
		optionsTable.add(ampLabel).fill();
		optionsTable.add(ampSlider).fill();

		// Persistance

		VisLabel persLabel = new VisLabel("Persistance: ", optionLabelStyle);
		persLabel.setAlignment(Align.right);
		persSlider = new VisSlider(0, 1f, 0.01f, false, sliderStyle);
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

	long totalLoad;
	long current;

	public void updateLoadingStatus(String s) {
		loadStateLabel.setText(s);
	}

	/**
	 * Sets load bar progress. setTotalLoadValue() must be called first must be given a float between 0.0 and the total load value
	 */
	public void updateLoadProgress(int p) {
		current = p;
		progress.setValue((float) ((double) current / (double) totalLoad) * 100.0f);
	}

	public void addLoadCycle() {
		current++;
		progress.setValue((float) ((double) current / (double) totalLoad) * 100.0f);
	}

	public void addLoadCycle(int cycles) {
		current += cycles;
		progress.setValue((float) ((double) current / (double) totalLoad) * 100.0f);
	}

	public void setTotalLoadValue(long i) {
		totalLoad = i;
	}

	public void startLoading(long i) {
		totalLoad = i;
		loading = true;
		progress.setValue(0);
	}

	public void endLoading() {
		loading = false;
	}

	public void generateIslandWithSettings() {
		IslandGenerator.OCTAVES = (int) octSlider.getValue();
		IslandGenerator.AMPLITUDE = ampSlider.getValue();
		IslandGenerator.FREQUENCY = freqSlider.getValue();
		IslandGenerator.PERSISTANCE = persSlider.getValue();

		island.beachBiomeSize = beachSizeSlider.getValue();
		island.waterLevel = waterLevelSlider.getValue();
		island.seed = seedText.getText().hashCode();
		island.overx = overxSlider.getValue();
		island.plusx = plusxSlider.getValue();
		island.peaks = (int) peakSlider.getValue();
	}
	
	public void setIslandName(String name){
		islandNameLabel.setText(name);
	}
}
