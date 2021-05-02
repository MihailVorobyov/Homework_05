import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainClass {
	public static final int CARS_COUNT = 4;
	static final Semaphore tunnelSemaphore = new Semaphore(CARS_COUNT / 2);
	static final Object beforeStartMon = new Object();
	static final CountDownLatch beforeStartLatch = new CountDownLatch(CARS_COUNT);
	static final CountDownLatch beforeEndLatch = new CountDownLatch(CARS_COUNT);
	static Lock lock = new ReentrantLock();


	public static void main(String[] args) {
		System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
		Race race = new Race(new Road(60), new Tunnel(), new Road(40));
		Car[] cars = new Car[CARS_COUNT];
		for (int i = 0; i < cars.length; i++) {
			cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
		}
		for (int i = 0; i < cars.length; i++) {
			new Thread(cars[i]).start();
		}
		try {
			beforeStartLatch.await();
				System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
				synchronized (beforeStartMon) {
				beforeStartMon.notifyAll();
			}

			// Только после того как все завершат гонку, нужно выдать объявление об окончании.
			beforeEndLatch.await();
			System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}