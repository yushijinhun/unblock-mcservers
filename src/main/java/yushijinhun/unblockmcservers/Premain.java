package yushijinhun.unblockmcservers;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class Premain {

	private static final Logger LOGGER = Logger.getLogger(Premain.class.getCanonicalName());

	public static void premain(String arg, Instrumentation instrumentation) {
		instrumentation.addTransformer(new BlockedServersTransformer());
		LOGGER.info("Transformer has been registered");
	}

}
