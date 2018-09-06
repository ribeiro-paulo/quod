/**
 * @author Gladson Souza de Araújo
 * @author Paulo Victor Ribeiro da Silva
 * 
 */

package game.main;

import game.component.Util;
import game.sound.Sound;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFrame;

public class QuodGame extends JFrame implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;
	public boolean status = false;
	public boolean[] keyControl;

	public static QuodGame qg;

	public Settings settings;
	public Control control;
	public Phase phase;
	public MainMenuScreen menu;
	public Loading loading;
	public GameOver over;
	public Phase phaseAgain;
	public Stop stop;
	public StopGame sp;

	/*
	 * Construtor
	 */
	public QuodGame() {

		settings = new Settings();
		loading = new Loading();
		over = new GameOver();
		phase = new Phase("res\\background\\galaxy_background01.jpg", 0);
		menu = new MainMenuScreen();
		control = new Control();
		
		
		setTitle("Quod - The Game");
		setSize(Util.DEFAULT_SCREEN_WIDTH, Util.DEFAULT_SCREEN_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

		// adiciona a tela de carregando
		add(loading);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// fecha o JPanel da Tela de carregando e abre o menu
		loading.setVisible(false);

		mainMenu(false);
	}

	/*
	 * M�todo de chamada do menu, cria todos os botoes.
	 */
	public void mainMenu(boolean aNew) {

		/*
		 * Para voltar para o menu, deve-se instanciar todos os botoes (true)
		 */
		if (aNew == true) {
			settings = new Settings();
			loading = new Loading();
			over = new GameOver();
			phase = new Phase("res\\background\\galaxy_background01.jpg", 0);
			menu = new MainMenuScreen();
			control = new Control();
		}

		phase.addKeyListener(this);
		phase.setFocusable(true);

		// Leitura dos botoes do menu
		menu.jbPlay.addActionListener(this);
		menu.jbBack.addActionListener(this);
		menu.jbControl.addActionListener(this);
		menu.jbControl.addActionListener(this);
		menu.jbSettings.addActionListener(this);
		control.jbComeBack.addActionListener(this);
		settings.jbComeBack.addActionListener(this);
		settings.jbVolume.addActionListener(this);
		settings.jbEffects.addActionListener(this);
		phase.jbStop.addActionListener(this);
		
		keyControl = new boolean[4];

		this.add(this.menu);
		menu.requestFocus();

	}

	/*
	 * Metodo Principal
	 */
	public static void main(String[] args) {
		qg = new QuodGame();
		qg.gameStart();
	}

	/*
	 * GameLopp
	 */
	@SuppressWarnings("deprecation")
	private void gameStart() {

		while (Util.PLAYING) {

			if (!Util.STOP) {

				gameControl();
				repaint();

				

				// contador disparos

				if (Util.SHOOT_COUNT < 10)
					Util.SHOOT_COUNT++;

			}
			

			// Tempo de atualiza��o da tela
			try {
				Thread.sleep(45);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// abrindo tela de fim de jogo
		Util.SOUND_PHASE.stop();
		Util.STOP = true;
		phase.timerEnemy.stop();
		phase.phaseClear();
		phase.setVisible(false);
		this.add(this.over);
		over.requestFocus();

		// botao de finalizar
		over.jbFinish.addActionListener(this);
		over.jbTrayAgain.addActionListener(this);
	}

	/* Teclado */

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		setKey(key, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		setKey(key, false);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/*
	 * Muda o estado da tecla pressionada
	 */
	public void setKey(int key, boolean status) {
		switch (key) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			keyControl[0] = status;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			keyControl[1] = status;
			break;
		case KeyEvent.VK_SPACE:
			keyControl[2] = status;
			break;
		case KeyEvent.VK_ESCAPE:
			keyControl[3] = status;
			break;
		}
	}

	/*
	 * Faz a verificacao do teclado
	 */
	private void gameControl() {
		// Esqurda
		if (keyControl[0]) {
			phase.player.moveLeft();
		}

		// Direita
		if (keyControl[1]) {
			phase.player.moveRight();
		}

		// Disparos
		if (keyControl[2] && Util.SHOOT_COUNT > 9) {
			phase.player.setShoot(true);

			if (Util.STATUS_EFFECTS) {
				new Sound(new File("res\\sound\\shoot.mp3")).start();
			}

			Util.SHOOT_COUNT = 0;
		}

	}
	
	

	/* Botoes */

	@Override
	public void actionPerformed(ActionEvent e) {

		// Jogar botao
		if (e.getSource() == menu.jbPlay) {

			if (Util.STATUS_SOUND) {
				Util.SOUND_PHASE.start();
			}

			menu.setVisible(false);

			add(phase);
			phase.timerEnemy.start();
			phase.requestFocus();

		}

		// Sair botao
		if (e.getSource() == menu.jbBack) {
			System.exit(0);
		}

		// Controle botao

		if (e.getSource() == menu.jbControl) {

			menu.setVisible(false);
			this.add(this.control);
			control.requestFocus();
		}

		// Controles / botao voltar
		if (e.getSource() == control.jbComeBack) {

			control.setVisible(false);
			mainMenu(true);

		}

		// fase / botao pausar
		if (e.getSource() == phase.jbStop) {
			if (Util.STOP) {
				Util.STOP = false;

				phase.timerEnemy.restart();

				phase.addKeyListener(this);
				phase.requestFocus();
				
			} else {
				
				Util.STOP = true;

				phase.timerEnemy.stop();

				new StopGame(phase);
				phase.addKeyListener(this);
				phase.requestFocus();

			}
		}
		
		

		// botao ajustes
		if (e.getSource() == menu.jbSettings) {
			menu.setVisible(false);
			this.add(this.settings);
			settings.requestFocus();

		}

		// Ajustes / botao de voltar
		if (e.getSource() == settings.jbComeBack) {
			settings.setVisible(false);
			this.add(this.menu);
			menu.requestFocus();

			mainMenu(true);
		}

		// Ajustes / botao de musica
		if (e.getSource() == settings.jbVolume) {
			if (Util.STATUS_SOUND)
				Util.STATUS_SOUND = false; // mudo

			else
				Util.STATUS_SOUND = true; // som
		}

		// Ajustes / Botao efeitos especiais
		if (e.getSource() == settings.jbEffects) {
			if (Util.STATUS_EFFECTS)
				Util.STATUS_EFFECTS = false; // mudo

			else
				Util.STATUS_EFFECTS = true; // som
		}

		// Fim de Jogo / Recome�ar
		if (e.getSource() == over.jbTrayAgain) {

		}

		// Fimde Jogo / Finalizar
		if (e.getSource() == over.jbFinish) {
			System.exit(0);
		}

	}

}
