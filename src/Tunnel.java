public class Tunnel extends Stage {
	public Tunnel() {
		this.length = 80;
		this.description = "Тоннель " + length + " метров";
	}
	@Override
	public void go(Car c) {

		try {
			System.out.println(c.getName() + " готовится к этапу(ждет): " + description);

			// В туннель не может заехать одновременно больше половины участников (условность).
			MainClass.tunnelSemaphore.acquire();
			System.out.println(c.getName() + " начал этап: " + description);
			Thread.sleep(length / c.getSpeed() * 1000);
			System.out.println(c.getName() + " закончил этап: " + description);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			MainClass.tunnelSemaphore.release();
		}
	}
}
