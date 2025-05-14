import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    public static boolean gameStarted = false; // Indicateur si le jeu a commencé
    private Space_ship spaceShip; // Instance du vaisseau
    private BufferedImage spaceBackground; // Fond de l'espace
    private int scrollSpeed = 2; // Vitesse de défilement du fond
    private int y1, y2; // Positions verticales pour le défilement
    private int x1, x2; // Positions horizontales pour le défilement
    private boolean sideView = false; // Indicateur de la vue (face ou côté)
    private boolean initialized = false; // Indicateur si le panneau est initialisé
    private boolean inTransition = false; // Indicateur si une transition est en cours
    private float alphaTransition = 0f; // Progression de la transition (alpha pour transparence)
    private boolean switchToSideView; // Indicateur si on passe à la vue côté
    private boolean showWarning = false; // Indicateur pour afficher un avertissement lors du changement de vue

    private final int maxMeteorites = 10; // Nombre maximum de météorites
    private Meteorites[] meteorites = new Meteorites[maxMeteorites]; // Tableau de météorites
    private int frameCounter = 0; // Compteur de frames pour les actions périodiques
    private boolean meteoritesActive = true;
    private WindowGame windowGame;
    private long lastHitTime = 0;
    private final int invulnerabilityDuration = 2000; // 2 seconde d'invulnérabilité après un hit
    // Indicateur si les météorites sont actives
    private boolean gameOver = false;
    private Thread gameThread;
    private ArrayList<Explosion> explosions = new ArrayList<>();


    private Items currentItem = null;
    private int itemSpawnCounter = 0;
    private final int spawnDelayFrames = 1800; // 30 sec à 60 FPS

    private TrapShip trapShip = new TrapShip();
    private long lastTrapSpawn = System.currentTimeMillis();
    private boolean showTrapWarning = false; // Affiche "!" avant l'arrivée du trap
    private long trapWarningStartTime = 0;

    private boolean trapShipWarningActive = false;




    // Méthode pour alterner la vue (de face à côté et vice-versa)
    public void switchView() {
        if (inTransition) return; // Si une transition est déjà en cours, on ne peut pas en démarrer une nouvelle

        showWarning = true; // Affiche un avertissement pendant la transition
        repaint(); // Repeint le panneau

        meteoritesActive = false; // Désactive les météorites pendant la transition
        clearMeteorites(); // Supprime les météorites actuelles

        // Crée un nouveau thread pour gérer la transition en arrière-plan
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Attend 5 secondes avant de commencer la transition
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showWarning = false; // Masque l'avertissement
            inTransition = true; // Marque la transition comme active
            switchToSideView = !sideView; // Change la vue (si on était de face, on passe de côté, et vice-versa)
            alphaTransition = 0f; // Réinitialise la progression de la transition

            // Anime la transition en augmentant progressivement l'alpha
            while (alphaTransition < 1f) {
                alphaTransition += 0.05f; // Incrémente l'alpha pour un effet de fondu
                spaceShip.setTransitionProgress(alphaTransition, switchToSideView); // Applique l'effet de transition au vaisseau
                repaint(); // Repeint le panneau
                try {
                    Thread.sleep(30); // Pause pour un effet visuel fluide
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sideView = switchToSideView; // Change effectivement la vue
            spaceShip.setVueChangee(sideView); // Applique la nouvelle vue au vaisseau

            // Configure les positions du fond en fonction de la vue (face ou côté)
            if (sideView) {
                x1 = 0;
                x2 = -getWidth(); // En vue de côté, on déplace le fond horizontalement
            } else {
                y1 = 0;
                y2 = -getHeight(); // En vue de face, on déplace le fond verticalement
            }

            // Anime la fin de la transition en diminuant progressivement l'alpha
            while (alphaTransition > 0f) {
                alphaTransition -= 0.05f;
                spaceShip.setTransitionProgress(alphaTransition, switchToSideView);
                repaint();
                try {
                    Thread.sleep(30); // Pause pour un effet visuel fluide
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            spaceShip.resetTransition(); // Réinitialise la transition du vaisseau
            inTransition = false; // La transition est terminée

            meteoritesActive = true; // Réactive les météorites
            scheduleNextViewChange(); // Planifie le prochain changement de vue
        }).start();
    }

    // Méthode pour planifier le prochain changement de vue après un délai aléatoire
    private void scheduleNextViewChange() {
        int minDelay = 2 * 60 * 1000; // Délai minimum entre les changements (2 minutes)
        int maxDelay = 3 * 60 * 1000; // Délai maximum entre les changements (3 minutes)
        int delay = minDelay + (int)(Math.random() * (maxDelay - minDelay)); // Délai aléatoire
        new Timer(delay, e -> switchView()).start(); // Lance un Timer pour le prochain changement de vue
    }

    // Constructeur de la classe GamePanel
    public GamePanel(JFrame frame,WindowGame WindowGame) {
        this.windowGame = WindowGame;


        setFocusable(true); // Permet à la fenêtre de recevoir des événements de clavier
        requestFocusInWindow(); // Demande le focus pour recevoir les événements de clavier

        spaceShip = new Space_ship(frame); // Crée une instance du vaisseau
        spaceShip.spaceShipControl(this); // Active les contrôles pour le vaisseau

        // Charge l'image du fond de l'espace
        try {
            spaceBackground = ImageIO.read(new File("src/resources/images/Space/spaceBackgroundGame.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialise les météorites
        for (int i = 0; i < maxMeteorites; i++) {
            meteorites[i] = new Meteorites();
        }

        // Initialise un Timer pour vérifier si le panneau est prêt
        // Initialise un Timer pour vérifier si le panneau est prêt
        Timer initTimer = new Timer(50, e -> {
            if (getWidth() > 0 && getHeight() > 0 && spaceBackground != null && !initialized) {
                initialized = true;

                y1 = 0;
                y2 = -getHeight();
                x1 = 0;
                x2 = -getWidth();

                // Crée et démarre un thread pour gérer les mises à jour du jeu
                gameThread = new Thread(() -> {
                    if(gameOver){
                        return;
                    }
                    while (!Thread.currentThread().isInterrupted()) {
                        // Mise à jour des positions du fond
                        if (sideView) {
                            x1 += scrollSpeed;
                            x2 += scrollSpeed;
                            if (x1 >= getWidth()) x1 = x2 - getWidth();
                            if (x2 >= getWidth()) x2 = x1 - getWidth();
                        } else {
                            y1 += scrollSpeed;
                            y2 += scrollSpeed;
                            if (y1 >= getHeight()) y1 = y2 - getHeight();
                            if (y2 >= getHeight()) y2 = y1 - getHeight();
                        }

                        // Met à jour la position du vaisseau
                        spaceShip.updatePosition(this);
                        spaceShip.isInvulnerable();

                        // Appelle la vérification des collisions
                        verifyIfCollision(spaceShip, meteorites);

                        frameCounter++;
                        if (meteoritesActive && frameCounter % 60 == 0) {
                            spawnMeteorites();
                        }

                        // Met à jour les météorites et vérifie les collisions pour chaque météorite
                        for (Meteorites m : meteorites) {
                            m.update();
                        }
                        // Gestion de l'apparition des items toutes les 30 secondes
                        itemSpawnCounter++;
                        if (itemSpawnCounter >= spawnDelayFrames) {
                            spawnItem();
                            itemSpawnCounter = 0;
                        }

                        // Gestion du trapShip
                        long now = System.currentTimeMillis();
                        if (!trapShip.isActive() && now - lastTrapSpawn >= 60000) {
                            if (!showTrapWarning) {
                                showTrapWarning = true;
                                trapWarningStartTime = now;
                            } else if (now - trapWarningStartTime >= 2000) {
                                trapShip.spawn(getWidth(), getHeight(), sideView);
                                lastTrapSpawn = now;
                                showTrapWarning = false;
                            }
                        } else if (trapShip.isActive()) {
                            trapShip.update(sideView);
                        }




                        repaint(); // Redessine le panneau après la mise à jour
                        try {
                            Thread.sleep(16); // Petit délai pour rendre l'animation fluide
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt(); // Bonne pratique pour gérer l'interruption
                        }
                    }
                });
                gameThread.start(); // Démarre le thread

                // Planifie le premier changement de vue après un délai aléatoire
                int firstDelay = 60_000 + (int)(Math.random() * 120_000);
                new Timer(firstDelay, e2 -> switchView()).start();
            }
        });
        initTimer.start(); // Démarre le Timer pour initialiser le jeu

    }

    // Méthode pour dessiner les éléments du jeu sur l'écran
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spaceBackground == null || !initialized) return;

        // Affichage du fond en fonction de la vue
        if (sideView) {
            g.drawImage(spaceBackground, x1, 0, getWidth(), getHeight(), null);
            g.drawImage(spaceBackground, x2, 0, getWidth(), getHeight(), null);
        } else {
            g.drawImage(spaceBackground, 0, y1, getWidth(), getHeight(), null);
            g.drawImage(spaceBackground, 0, y2, getWidth(), getHeight(), null);
        }

        // Dessin des météorites
        for (Meteorites m : meteorites) {
            if(m.isActive()){
                m.draw(g);
            }
        }

        updateExplosions(g);

        if (currentItem != null && currentItem.isActive()) {
            currentItem.draw(g);
        }
        // Dessin du vaisseau
        spaceShip.dessiner(g);


        // Affichage du trapShip
        if (trapShip.isActive()) {
            trapShip.draw(g);
        }

        // Affichage de l'avertissement "!" pour trapShip
        if (showTrapWarning) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            g2d.setColor(Color.RED);
            String warning = "!";
            int strWidth = g2d.getFontMetrics().stringWidth(warning);
            g2d.drawString(warning, (getWidth() - strWidth) / 2, getHeight() / 2);
        }




        // Affichage de la transition si elle est en cours
        if (inTransition) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, alphaTransition)); // Application de la transparence
            g2d.fillRect(0, 0, getWidth(), getHeight()); // Dessine un rectangle noir semi-transparent
        }

        // Affichage de l'avertissement avant le changement de vue
        if (showWarning) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("Arial", Font.BOLD, 30)); // Définition de la police
            g2d.setColor(Color.RED); // Couleur de l'avertissement
            String warning = "⚠ View change imminent ⚠"; // Message d'avertissement
            int strWidth = g2d.getFontMetrics().stringWidth(warning);
            g2d.drawString(warning, (getWidth() - strWidth) / 2, 50); // Affiche le message centré
        }
    }

    // Méthode pour faire apparaître des météorites si nécessaire
    private void spawnMeteorites() {
        int activeCount = 0;
        for (Meteorites m : meteorites) {
            if (m.isActive()) activeCount++;
        }

        if (activeCount < maxMeteorites) {
            for (Meteorites m : meteorites) {
                if (!m.isActive()) {
                    m.spawn(getWidth(), getHeight(), sideView, meteorites); // Spawn d'une nouvelle météorite
                    break;
                }
            }
        }
    }

    // Méthode pour effacer les météorites du jeu
    private void clearMeteorites() {
        for (Meteorites m : meteorites) {
            m.deactivate(); // Désactive chaque météorite
        }
    }

    // Méthode pour vérifier les collisions entre le vaisseau et les météorites
    public void verifyIfCollision(Space_ship spaceShip, Meteorites[] meteorites) {
        if (gameOver) {
            return;
        }

        try {
            Polygon shipPolygon = spaceShip.getPolygon();

            // --- Collision vaisseau / météorite ---
            for (Meteorites m : meteorites) {
                if (m.isActive()) {
                    Polygon meteorPolygon = m.getPolygon();

                    if (shipPolygon.intersects(meteorPolygon.getBounds())) {
                        if (!spaceShip.isInvulnerable()) {
                            System.out.println("Collision avec météorite !");
                            windowGame.loseLife();
                            spaceShip.setInvulnerable();
                        }
                        m.setActive(false);
                        break;
                    }
                }
            }

            // --- Collision météorite / météorite ---
            for (int i = 0; i < meteorites.length; i++) {
                Meteorites m1 = meteorites[i];
                if (!m1.isActive()) continue;
                Polygon p1 = m1.getPolygon();

                for (int j = i + 1; j < meteorites.length; j++) {
                    Meteorites m2 = meteorites[j];
                    if (!m2.isActive()) continue;
                    Polygon p2 = m2.getPolygon();

                    if (p1.intersects(p2.getBounds())) {
                        m1.setActive(false);
                        m2.setActive(false);

                        int centerX = (m1.getX() + m2.getX()) / 2;
                        int centerY = (m1.getY() + m2.getY()) / 2;
                        int explosionWidth = Math.max(m1.getWidth(), m2.getWidth()) + 200;
                        int explosionHeight = Math.max(m1.getHeight(), m2.getHeight()) + 200;

                        int explosionX = centerX - explosionWidth / 2;
                        int explosionY = centerY - explosionHeight / 2;

                        createExplosion(explosionX, explosionY, explosionWidth, explosionHeight);

                        break;
                    }
                }
            }

            // --- Collision vaisseau / explosion ---
            for (Explosion explosion : explosions) {
                if (explosion.isActive()) {
                    Rectangle explosionRect = new Rectangle(
                            explosion.getX(),
                            explosion.getY(),
                            explosion.getWidth(),
                            explosion.getHeight()
                    );

                    if (explosionRect.intersects(shipPolygon.getBounds())) {
                        if (!spaceShip.isInvulnerable()) {
                            System.out.println("Collision avec explosion !");
                            windowGame.loseLife();
                            spaceShip.setInvulnerable();
                        }
                        break;
                    }
                }
            }

            if(trapShip.isActive()){

                Polygon trapHitbox = trapShip.getHitbox();
                if (shipPolygon.intersects(trapHitbox.getBounds())) {
                    if (!spaceShip.isInvulnerable()) {
                        System.out.println("Collision avec TrapShip !");
                        windowGame.loseLife();
                        spaceShip.setInvulnerable();
                    }
                    trapShip.setActive(false);  // Désactive le TrapShip après collision
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void gameOver() {
        gameOver = true;  // Marque le jeu comme terminé
        // Ici, tu peux ajouter des actions supplémentaires, comme afficher "Game Over"
    }

    public void stopGameThread() {
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
    }

    public void createExplosion(int x, int y, int width, int height){
        explosions.add(new Explosion(x, y, width, height));

    }

    public void updateExplosions(Graphics g){
        for(int i = 0; i < explosions.size(); i++){
            Explosion explosion = explosions.get(i);
            explosion.update();

            if(!explosion.isActive()){
                explosions.remove(i);
                i--;
            }
        }

        for(Explosion explosion : explosions){
            if(explosion.isActive()){
                explosion.draw(g);

            // Affiche la hitbox circulaire en rouge
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.RED);
                g2d.draw(explosion.getHitbox());
            }
        }

    }
    private void spawnItem(){
        if (currentItem == null || !currentItem.isActive()) {
            String[] itemTypes = {"heart", "laser" , "laser" ,"laser"};
            int index = (int)(Math.random() * itemTypes.length);
            String type = itemTypes[index];

            currentItem = new Items(type);
            currentItem.spawn(getWidth(), getHeight());

            Thread despawnThread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    if (currentItem != null && currentItem.isActive()) {
                        currentItem.deactivate();
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            despawnThread.start();
        }
    }

    private void updateTrapShip() {
        long now = System.currentTimeMillis();
        long timeSinceLastSpawn = now - lastTrapSpawn;

        // Affiche l'avertissement 2 secondes avant l'apparition
        if (!trapShip.isActive() && timeSinceLastSpawn > 58_000 && timeSinceLastSpawn < 60_000) {
            showTrapWarning = true;
            trapWarningStartTime = now;
        }

        // Fait apparaître le trapShip après 60 secondes
        if (!trapShip.isActive() && timeSinceLastSpawn >= 60_000) {
            trapShip.spawn(getWidth(), getHeight(), sideView); // Méthode spawn à créer dans TrapShip
            lastTrapSpawn = now;
            showTrapWarning = false;
        }

        // Met à jour le vaisseau-piège s'il est actif
        if (trapShip.isActive()) {
            trapShip.update(sideView);
        }
    }






}