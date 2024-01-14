/*
 * ENUNCIADO: 
 * En una barbería, hay un barbero que corta el pelo a los clientes y un conjunto de sillas de
    espera para los clientes. Si no hay clientes para ser atendidos, el barbero se sienta en su silla
    y se duerme. Cuando llega un cliente, si el barbero está dormido, el cliente lo despierta y se
    corta el pelo. Si el barbero está ocupado atendiendo a otros clientes, el cliente nuevo se sienta
    en una de las sillas de espera. Si todas las sillas de espera están ocupadas, el cliente se va.
    Una vez que el barbero termina de cortar el pelo a un cliente, el siguiente cliente en la silla de
    espera se despierta si hay alguno y se corta el pelo, o el barbero se duerme de nuevo si no
    hay más clientes
 */


public class ProblemaDelBarbero {
    public static void main(String[] args) {
        Barberia barberia = new Barberia();

        // Llegada de los clientes
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep((long) (Math.random() * 2000));
                new Thread(() -> {
                    try {
                        barberia.llegarCliente();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Inicio del trabajo del barbero
        new Thread(barberia::trabajar).start();
    }
}
