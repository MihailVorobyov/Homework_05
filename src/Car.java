import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
	private static int CARS_COUNT;
	private static final CyclicBarrier beforeStartBarrier;
	private static boolean haveWinner;

	static {
		CARS_COUNT = 0;
		beforeStartBarrier = new CyclicBarrier(MainClass.CARS_COUNT);
	}

	private final Race race;
	private final int speed;
	private final String name;

	public String getName() {
		return name;
	}

	public int getSpeed() {
		return speed;
	}

	public Car(Race race, int speed) {
		this.race = race;
		this.speed = speed;
		CARS_COUNT++;
		this.name = "Участник #" + CARS_COUNT;
		haveWinner = false;
	}

	@Override
	public void run() {
		try {
			System.out.println(this.name + " готовится");
			Thread.sleep(500 + (int) (Math.random() * 800));
			System.out.println(this.name + " готов");

			// Все участники должны стартовать одновременно, несмотря на то, что на подготовку у каждого из них уходит разное время.
			beforeStartBarrier.await();
			MainClass.beforeStartLatch.countDown();

			// переход к System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
			synchronized (MainClass.beforeStartMon) {
				MainClass.beforeStartMon.wait();
			}

			for (int i = 0; i < race.getStages().size(); i++) {
				race.getStages().get(i).go(this);

				MainClass.lock.lock();
				if (i == race.getStages().size() - 1 && !haveWinner) {
					System.out.println(this.name + " - WIN");
					haveWinner = true;
				}
				MainClass.lock.unlock();
			}

			MainClass.beforeEndLatch.countDown();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}