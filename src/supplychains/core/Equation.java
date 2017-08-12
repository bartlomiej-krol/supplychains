package supplychains.core;

import java.util.List;


public class Equation {

	List<Trip> add;
	List<Trip> sub;
	Sign sign;
	double val;
	
	
	
	
	public Equation(List<Trip> add, List<Trip> sub, Sign sign,double val) {
		super();
		this.add = add;
		this.sub = sub;
		this.sign = sign;
		this.val = val;
	}

	public String getStringVal(){
		StringBuilder sb = new StringBuilder();
		for(Trip t : add)
			sb.append((sb.length() > 0 ? " + " : "") + t.name);
		for(Trip t : sub)
			sb.append((sb.length() > 0 ? " - " : "") + t.name);
		sb.append(" " + sign.txt);
		sb.append(" " + val);
		return sb.toString();
	}


	public enum Sign{
		LESS("<="),
		MORE(">="),
		EQUAL("=");
		
		private Sign(String txt) {
			this.txt = txt;
		}

		final String txt;
		
		
	}
}
