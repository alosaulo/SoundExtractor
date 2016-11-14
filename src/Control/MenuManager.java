package Control;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import View.SingleWaveformPanel;
import View.WaveformPanelContainer;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

public class MenuManager {
	JFileChooser chooser;
	File source;
	File target;
	WaveformPanelContainer container;
	AudioInputStream audioInputStream;
	
	public MenuManager(){
		chooser =  new JFileChooser();
	}
	
	public void OpenFile(){
		JOptionPane.showMessageDialog(null, "Selecione um arquivo de vídeo.");
		chooser.showOpenDialog(null); 
		source = chooser.getSelectedFile();
	}
	
	public void ExtractAudio() throws Exception{
		JOptionPane.showMessageDialog(null, "Escolha um diretório para extrair o áudio, também escreva o nome do arquivo e o seu formato.");
		chooser.showSaveDialog(null);
		File target = chooser.getSelectedFile();
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("pcm_s16le");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("wav");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		try {
			encoder.encode(source, target, attrs);
			AudioVisualizer(target);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AudioVisualizer(File file) throws Exception{
		try{
			JFrame frame = new JFrame("Waveform Display Simulator"); 
			frame.setBounds(200,200, 500, 350);
			InputStream audioSrc = new FileInputStream(file);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
			//AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			container = new WaveformPanelContainer();
			container.setAudioToDisplay(audioInputStream);
			frame.getContentPane().setLayout(new BorderLayout());		
			frame.getContentPane().add(container, BorderLayout.CENTER);
		
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setVisible(true);
			frame.validate();
			frame.repaint();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	public ArrayList<SilenceInfo> CheckSilence(String MaxDB, String SecThreshold) throws Exception{
		ArrayList<SingleWaveformPanel> arr = container.GetWaveformPanels();
		ArrayList<SilenceInfo> silenceInfo = new ArrayList<SilenceInfo>();
		AudioFormat format = audioInputStream.getFormat();
		int counter = 0;
		double db = Double.parseDouble((String) MaxDB);
		double secs = Double.parseDouble((String) SecThreshold);
		silenceInfo = arr.get(0).CheckSilence(format,secs,db);
		JOptionPane.showMessageDialog(null, "Busca concluida.");
		return silenceInfo;
	}
	
}
