package View;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;

import Control.SilenceInfo;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class SingleWaveformPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Color BACKGROUND_COLOR = Color.gray;
    protected static final Color REFERENCE_LINE_COLOR = Color.blue;
    protected static final Color WAVEFORM_COLOR = Color.blue;
    public ArrayList<DrawnSample> drawnSamples = new ArrayList<DrawnSample>();
    
    private Audio_Info helper;
    private int channelIndex;
    private int[] samples;
    
    public SingleWaveformPanel(Audio_Info helper, int channelIndex) {
        this.helper = helper;
        this.channelIndex = channelIndex;
        setBackground(BACKGROUND_COLOR);
    }
    
    public int[] GetSamples(){
    	return this.samples;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawnSamples.clear();
        int lineHeight = getHeight() / 2;
        g.setColor(REFERENCE_LINE_COLOR);
        g.drawLine(0, lineHeight, (int)getWidth(), lineHeight);
        drawWaveform(g, helper.getAudio(channelIndex));
    }
    
    public void drawTheBeach(SilenceInfo silenceInfo){
    	int proportionalS =(int) (silenceInfo.GetStartIndex()*helper.getXScaleFactor(getWidth())/samples.length);
    	int proportionalE =(int) (silenceInfo.GetEndIndex()*helper.getXScaleFactor(getWidth())/samples.length);
    	DrawnSample drawnS = drawnSamples.get((int) (silenceInfo.GetStartIndex()/helper.getXScaleFactor(getWidth())));
    	DrawnSample drawnE = drawnSamples.get((int) (silenceInfo.GetEndIndex()/helper.getXScaleFactor(getWidth())));
    	this.getGraphics().drawRect(silenceInfo.GetStartIndex(), getHeight()/2, 30, 30);
    	this.getGraphics().drawRect(silenceInfo.GetEndIndex(), getHeight()/2, 30, 30);
    }
    
    protected void drawWaveform(Graphics g, int[] samples) {
        if (samples == null) {
            return;
        }else
        	this.samples = samples;

        int oldX = 0;
        int oldY = (int) (getHeight() / 2);
        int xIndex = 0;
        
        int increment = helper.getIncrement(helper.getXScaleFactor(getWidth()));
        g.setColor(WAVEFORM_COLOR);

        int t = 0;

        for (t = 0; t < increment; t += increment) {
            g.drawLine(oldX, oldY, xIndex, oldY);
            drawnSamples.add(new DrawnSample(oldX,oldY,xIndex,oldY));
            xIndex++;
            oldX = xIndex;
        }

        for (; t < samples.length; t += increment) {
            double scaleFactor = helper.getYScaleFactor(getHeight());
            double scaledSample = samples[t] * scaleFactor;
            int y = (int) ((getHeight() / 2) - (scaledSample));
            g.drawLine(oldX, oldY, xIndex, y);
            drawnSamples.add(new DrawnSample(oldX,oldY,xIndex,y));
            xIndex++;
            oldX = xIndex;
            oldY = y;
        }
    }
    /*standard range of audible frequencies is 20 to 20,000 Hz*/
    public ArrayList<SilenceInfo> CheckSilence(AudioFormat format, double Secs, double db) throws Exception{
    	ArrayList<SilenceInfo> silenceInfo = new ArrayList<SilenceInfo>();
    	
		//22050 bytes equivalem a 0.500 segundos
		double thresholdInSecs = Secs;
		double maxDb = db;
		double byteRate = thresholdInSecs * format.getSampleRate();
		SilenceInfo silence = new SilenceInfo(-1);
    	int counter = 0;
    	try{
	    	if(samples != null){
				for(int i = 0; i < samples.length; i++){
					if(counter < byteRate){
						if(Math.abs(samples[i]) >= 0 && Math.abs(samples[i]) <= maxDb){
							counter = counter + 1;
						}else{
							if(silence.GetStart() != -1){
								silence.SetEnd(i);
								silence.CalculateDuration(format.getSampleRate());
								silenceInfo.add(silence);
								
								silence = new SilenceInfo(-1);
							}
							counter = 0;
						}
					}else{
						if(silence.GetStart() == -1)
							silence.SetStart(i-counter);
						silence.SetEnd(i);
						counter = 0;
					}
				}
			}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return silenceInfo;
    }
    
}