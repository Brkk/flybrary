package com.uvic.textshare.service.model;

import java.util.concurrent.TimeUnit;

public class Delay {

	public static void oneSecondDelay() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

}
