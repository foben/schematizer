package net.foben.schematizer.parse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements RDFHandler {
	protected int stcount = 0;
	protected Logger _log;
	protected long last, now;
	protected boolean started = false;
	private HashMap<String, Timing> timings;
	private int chunks;

	public AbstractHandler() {
		this(1);
	}

	public AbstractHandler(int chunks) {
		this._log = LoggerFactory.getLogger(this.getClass());
		this.chunks = chunks;
		last = System.nanoTime();
		timings = new HashMap<String, Timing>();
		_log.info("Called abstract constructor");
	}

	public void setChunks(int chunks) {
		if (started)
			throw new IllegalArgumentException("Parsing already started!");
		else {
			this.chunks = chunks;
		}
	}

	@Override
	public final void startRDF() throws RDFHandlerException {
		if (!started) {
			started = true;
			parseStart();
		}
		fileStart();
	}

	@Override
	public final void endRDF() throws RDFHandlerException {
		fileEnd();
		chunks -= 1;
		if (chunks < 0)
			throw new NumberFormatException("WRONG SHOULDNT HAPPEN!!");
		if (chunks == 0)
			parseEnd();
	}

	protected void parseStart() {
	}

	protected void fileStart() {
	}

	protected void fileEnd() {
	}

	protected void parseEnd() {
	}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {
	}

	@Override
	public void handleNamespace(String arg0, String arg1)
			throws RDFHandlerException {
	}

	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		stcount++;
		if (stcount % 1000000 == 0) {
			everyMillion();
			if (stcount % 100000000 == 0) {
				everyHundredMillion();
			}
		}

		handleStatementInternal(arg0);
	}

	protected void everyMillion() {
		_log.info(stcount / 1000000 + " million lines parsed. Speed: "
				+ measure());
		if (timings.keySet().size() > 0) {
			_log.info("Timings:");
			double total = 0;
			for (String key : timings.keySet()) {
				double current = timings.get(key).getTime();
				_log.info(" " + String.format("%-12s", key) + " - " + current);
				total += current;
				timings.get(key).reset();
			}
			_log.info(" --------");
			_log.info(" " + String.format("%-12s", "total") + " - " + total);
		}
	}

	protected void everyHundredMillion() {
	}

	public abstract void handleStatementInternal(Statement st);

	private String measure() {
		now = System.nanoTime();
		double dur = (now - last) / 1000000000d;
		String result = ("" + dur);
		last = now;
		return result;
	}

	protected void timingStart(String str) {
		Timing t = timings.get(str);
		if (t == null) {
			timings.put(str, new Timing());
		} else {
			t.tickStart();
		}
	}

	protected void timingEnd(String str) {
		Timing t = timings.get(str);
		if (t == null) {
			throw new IllegalArgumentException("Dont finish before start!");
		} else {
			t.tickEnd();
		}
	}

	private class Timing {

		private long lastTick, total;

		public Timing() {
			lastTick = System.nanoTime();
			total = 0;
		}

		public void tickStart() {
			lastTick = System.nanoTime();
		}

		public void tickEnd() {
			total += System.nanoTime() - lastTick;
		}

		public double getTime() {
			return total / 1000000000d;
		}

		public void reset() {
			total = 0;
		}

	}

	protected void writeToFile(Iterable<?> i, String filename) {
		_log.info("Serializing to " + filename);
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(filename));
			for (Object o : i) {
				br.write(o.toString());
				br.newLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
