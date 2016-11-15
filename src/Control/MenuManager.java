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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

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
	public WaveformPanelContainer container;
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
	
	public void CreateXML(ArrayList<SilenceInfo> silenceInfo){
		try
		{
		  DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		  //root elements
		  Document doc = docBuilder.newDocument();

		  Element rootElement = doc.createElement("SilenceInfo");
		  doc.appendChild(rootElement);
		  
		  for(int i = 0; i < silenceInfo.size(); i++){

			  Element row = doc.createElement("Row");
			  rootElement.appendChild(row);
			  row.setAttribute("id", Integer.toString(i));
			  
			  String info = Float.toString(silenceInfo.get(i).GetStart());
			  
			  Element start = doc.createElement("start");
			  start.appendChild(doc.createTextNode(info));
			  row.appendChild(start);
			  
			  info = Float.toString(silenceInfo.get(i).GetEnd());
			  Element end = doc.createElement("end");
			  end.appendChild(doc.createTextNode(info));
			  row.appendChild(end);
			  
			  info = Float.toString(silenceInfo.get(i).GetDuration());
			  Element duration = doc.createElement("duration");
			  duration.appendChild(doc.createTextNode(info));
			  row.appendChild(duration);
		  }
		  //write the content into xml file
		  TransformerFactory transformerFactory = TransformerFactory.newInstance();
		  Transformer transformer = transformerFactory.newTransformer();
		  DOMSource source = new DOMSource(doc);
		  JOptionPane.showMessageDialog(null, "Escolha um diretório e o nome do arquivo");
		  chooser.showSaveDialog(null);
		  StreamResult result =  new StreamResult(new File(chooser.getSelectedFile()+".xml"));
		  transformer.transform(source, result);

		  JOptionPane.showMessageDialog(null, "Feito.");

		}catch(ParserConfigurationException pce){
		  pce.printStackTrace();
		}catch(TransformerException tfe){
		  tfe.printStackTrace();
		}
	}
	
}
