package data_streamer;

import java.io.IOException;

public class Market_test
{

<<<<<<< HEAD
	public static void main(String[] args) {
		Market mkt = new Market("/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/data_streamer/market.config",
				"/Users/theocean154/Documents/School_files/College/Programs/eclipse/bda/src/data_streamer/data_config.txt",
				"./");
		Market mkt2 = new Market(" ", " ", "");
		
		
		try {
			mkt.loadConfigs();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			mkt.setupTimer();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		mkt.tick();
		System.out.println(mkt.toString());
		mkt.tick();
		System.out.println(mkt.toString());
		mkt.tick();
		System.out.println(mkt.toString());
		mkt.tick();
		System.out.println(mkt.toString());
		mkt.tick();
		System.out.println(mkt.toString());
		mkt.endLog();
		System.out.println(mkt.toString());
=======
    public static void main(String[] args)
    {
        Market mkt = new Market("src/config/market.config", "src/config/data_config.txt", "./");
>>>>>>> ce392b54c38b9e0a61ce28a22b1d700e437b1908

        try {
            mkt.loadConfigs();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            mkt.setupTimer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.tick();
        System.out.println(mkt.toString());
        mkt.endLog();
        System.out.println(mkt.toString());

    }

}
