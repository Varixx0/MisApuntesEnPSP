import java.util.concurrent.Semaphore;

class Barberia {
    //creamos la clase barberia donde ponemos todos los atributos que necesitamos para ejecutar el programa.
    private static final int MAX_CLIENTES = 5;
    private static final int NUM_SILLAS = 3;
    private int clientes = 0;
    private boolean barberoDuerme = true;
    //Aqui tenemos el mutex que en realidad equivale al barbero cortando el pelo. 
    private final Semaphore mutex = new Semaphore(1);
    //Segundo semaforo que controla el numero de sillas.
    private final Semaphore sillasEspera = new Semaphore(NUM_SILLAS);
    //El metodo llegar cliente controla toda la llegada de clientes. Comprueba si esta llena o no y si lo esta el cliente se va.
    public void llegarCliente() throws InterruptedException {
        //Cuando llega un cliente hace un mutex, supongo que para que no entren nuevos mientras procesamos a este.
        mutex.acquire();
        //Comprueba si los clientes caben en la barberia y si caben los gestiona
        if (clientes < MAX_CLIENTES) {
            clientes++;
            System.out.println("Llega un cliente. Clientes en espera: " + clientes);
            //Si el barbero duerme lo despierta y empieza a cortarle el pelo al cliente.
            if (barberoDuerme) {
                barberoDuerme = false;
                mutex.release();
                new Thread(this::cortarPelo).start();
            } else {
                //Si el barbero no esta durmiendo significa que esta cortando el pelo asi que se sienta y espera. 
                sillasEspera.acquire();
                mutex.release();
                new Thread(this::cortarPelo).start();
            }
        } else {
            //Si la barberia esta llena se va
            System.out.println("La barbería está llena. El cliente se va.");
            mutex.release();
        }
    }
    //Metodo cortar el pelo. Se sienta a cortarse el pelo y espera una cantidad aleatoria de tiempo
    public void cortarPelo() {
        try {
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("¡El cliente se está cortando el pelo!");
        //cuando acaba de cortarse el pelo coge al siguiente cliente y le corta el pelo?? No entiendo este codifo la vd
        try {
            mutex.acquire();
            clientes--;
            sillasEspera.release();
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    //El metodo trabajar controla el flujo de trabajo del barbero aunque no entiendo absolutamente nada de este codigo pero bueno
    public void trabajar() {
        while (true) {
            try {
                mutex.acquire();
                //Si no hay nadie esperando el barbero se duerme
                if (sillasEspera.availablePermits() == NUM_SILLAS) {
                    barberoDuerme = true;
                    mutex.release();
                    System.out.println("El barbero se ha dormido.");
                    //???
                    Thread.currentThread().join();
                } else {
                    mutex.release();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
