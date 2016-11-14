package Control;

public class SilenceInfo {
	private float duration = 0;
	private float start = 0;
	private float end = 0;
	
	private double startSec;
	private double endSec;
	private double durationTime;
	
	public void SetStart(float start){
		this.start = start;
	}
	
	public float GetStart(){
		return this.start;
	}
	
	public void SetEnd(float end){
		this.end = end;
	}
	
	public float GetEnd(){
		return this.end;
	}
	
	public float GetDuration(){
		return this.duration;
	}
	
	public SilenceInfo(float start){
		this.start = start;
	}
	
	public SilenceInfo(float start, float end){
		this.start = start;
		this.end = end;
	}
	
	public void CalculateDuration(double SampleRate){
		this.start = this.start/(float)SampleRate;
		this.end = this.end/(float)SampleRate;
		this.duration = (this.end - this.start);
	}
	
}
