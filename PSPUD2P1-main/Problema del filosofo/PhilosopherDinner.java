import java.util.Random;
import java.util.concurrent.Semaphore;

class Philosopher extends Thread {
    //Atributos of  filosofo
    private int id;
    private Semaphore forks[];
    //Contructor de la clase
    public Philosopher(int id, Semaphore[] forks) {
        this.id = id;
        this.forks = forks;
    }
    //El metodo pensar. 
    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking");
        Random random = new Random();
        int waitTime = random.nextInt(5000) + 1000; // Wait between 1 and 5 seconds
        //El hilo duertme durante el tiempo random que se ha generado
        Thread.sleep(waitTime); // Thread sleeps the time generated before
    }
    //Metodo comer. Literalmente lo mismo que pensar
    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating");
        Random random = new Random();
        int waitTime = random.nextInt(5000) + 1000; // Wait between 1 and 5 seconds
        Thread.sleep(waitTime); // Thread sleeps the time generated before
    }
    //El metodo run. El mas importante, Esto es lo que se ejecuta cuando haces start a un hilo 
    @Override
    public void run() {
        try {
            while (true) {
                // They all start thinking
                think();
                //Aqui vemos el uso del tryAquiere que basicamente intenta conseguir un recurso. Esto tiene que ver con los semaforos que luego se declaran
                if (forks[id].tryAcquire()) { //Coge el tenedor si puede
                    if (forks[(id + 1) % forks.length].tryAcquire()) { // Intenta conseguir el tenedor de la derecha
                        eat();//si consigue los dos come y luego los suelta ambos

                        forks[id].release(); // Leaves the rigth fork till he finishes eating
                        forks[(id + 1) % forks.length].release(); // Leaves the left fork
                    } else {
                        //Si coge el tenedor izquierdo pero no puede coger el derecho suelta el izquierdo y espera
                        forks[id].release();
                        // Tries again
                        Thread.sleep(new Random().nextInt(2000) + 1000);  // Wait between 1 and 3 miliseconds
                    }
                } else {
                    // Si no puede coger el primer tenedor mas de lo mismo. Lo suelta y espera
                    Thread.sleep(new Random().nextInt(2000) + 1000); // Wait between 1 and 3 miliseconds
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class PhilosopherDinner{
    public static void main(String[] args) {
        int numPhilosopher = 5;
        //Semaforos. Sirven para crear recursos limitados que los hilos pueden o no utilizar. En este caso los tenedores. Funciona como una array y cada una de las entradas esta ocupada con el tryAquiere o soltarlo con el release
        Semaphore[] forks = new Semaphore[numPhilosopher];
        Philosopher[] philosophers = new Philosopher[numPhilosopher];

        for (int i = 0; i < numPhilosopher; i++) {
            //crea un tenedor, es decir semaforo. Y le asignas los permits, que los permits significan la cantidad de personas pueden estar utilizando a la vez
            forks[i] = new Semaphore(1); // Start all forks to available
        }

        for (int i = 0; i < numPhilosopher; i++) {
            philosophers[i] = new Philosopher(i, forks);
            philosophers[i].start();
        }
    }
}