// Código para simulação de partículas com multithreading em Java

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SimulacaoParticulas extends JPanel {

    // Configurações da simulação
    private static final int LARGURA = 800;
    private static final int ALTURA = 600;
    private static final int NUM_PARTICULAS = 1000;
    private static final int NUM_THREADS = 4; // Número de threads

    private final Random random = new Random();
    private final Point[] particulas;
    private boolean executando = true; // Para controlar a execução

    public SimulacaoParticulas() {
        particulas = new Point[NUM_PARTICULAS];
        inicializarParticulas();
    }

    // Cria as partículas em posições aleatórias
    private void inicializarParticulas() {
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            int x = random.nextInt(LARGURA);
            int y = random.nextInt(ALTURA);
            particulas[i] = new Point(x, y);
        }
    }

    // Atualiza a posição das partículas
    private void atualizarParticulas(int inicio, int fim) {
        for (int i = inicio; i < fim; i++) {
            if (!executando) return; // Para caso o programa precise parar

            Point p = particulas[i];
            p.x += random.nextInt(5) - 2; // Movimento aleatório no eixo X
            p.y += random.nextInt(5) - 2; // Movimento aleatório no eixo Y

            // Mantém as partículas dentro da janela
            if (p.x < 0) p.x = 0;
            if (p.x >= LARGURA) p.x = LARGURA - 1;
            if (p.y < 0) p.y = 0;
            if (p.y >= ALTURA) p.y = ALTURA - 1;
        }
    }

    // Método para desenhar as partículas
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
          // Define o fundo como preto
          g.setColor(Color.BLACK);
          g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        for (Point p : particulas) {
            g.fillRect(p.x, p.y, 2, 2);
        }
    }

    public void iniciarSimulacao() {
        executando = true;
        Thread[] threads = new Thread[NUM_THREADS];
        int particulasPorThread = NUM_PARTICULAS / NUM_THREADS;

        for (int t = 0; t < NUM_THREADS; t++) {
            final int inicio = t * particulasPorThread;
            final int fim = (t == NUM_THREADS - 1) ? NUM_PARTICULAS : inicio + particulasPorThread;
            threads[t] = new Thread(() -> {
                while (executando) {
                    atualizarParticulas(inicio, fim);
                    repaint();
                    try {
                        Thread.sleep(16); // Aproximadamente 60 FPS
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            threads[t].start();
        }

        // Parar a simulação ao fechar a janela
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    executando = false;
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.exit(0);
                }
            });
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulação de Partículas");
        SimulacaoParticulas painel = new SimulacaoParticulas();

        frame.add(painel);
        frame.setSize(LARGURA, ALTURA);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        painel.iniciarSimulacao();
    }
}
