import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GamePlay {

    private final static int size = 15;
    private final static int dist = 70;

    private int n;
    private Board board;
    private int turn;
    private boolean mouseEnabled;

    GameSolver redSolver, blueSolver, greenSolver, solver;
    String redName, blueName, greenName;
    Main parent;

    private JLabel[][] hEdge, vEdge, box;
    private boolean[][] isSetHEdge, isSetVEdge;

    private JFrame frame;
    private JLabel redScoreLabel, blueScoreLabel, greenScoreLabel, statusLabel;

    private MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if(!mouseEnabled) return;
            processMove(getSource(mouseEvent.getSource()));
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            if(!mouseEnabled) return;
            Edge location = getSource(mouseEvent.getSource());
            int x=location.getX(), y=location.getY();
            if(location.isHorizontal()) {
                if(isSetHEdge[x][y]) return;
                if (turn == Board.RED)
                	hEdge[x][y].setBackground(Color.RED);
                else if (turn == Board.BLUE)
                	hEdge[x][y].setBackground(Color.BLUE);
                else
                	hEdge[x][y].setBackground(Color.GREEN);
            }
            else {
                if(isSetVEdge[x][y]) return;
                if (turn == Board.RED)
                	vEdge[x][y].setBackground(Color.RED);
                else if (turn == Board.BLUE)
                	vEdge[x][y].setBackground(Color.BLUE);
                else
                	vEdge[x][y].setBackground(Color.GREEN);
            }
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            if(!mouseEnabled) return;
            Edge location = getSource(mouseEvent.getSource());
            int x=location.getX(), y=location.getY();
            if(location.isHorizontal()) {
                if(isSetHEdge[x][y]) return;
                hEdge[x][y].setBackground(Color.GRAY);
            }
            else {
                if(isSetVEdge[x][y]) return;
                vEdge[x][y].setBackground(Color.GRAY);
            }
        }
    };

    private void processMove(Edge location) {
        int x=location.getX(), y=location.getY();
        ArrayList<Point> ret;
        if(location.isHorizontal()) {
            if(isSetHEdge[x][y]) return;
            ret = board.setHEdge(x,y,turn);
            hEdge[x][y].setBackground(Color.BLACK);
            isSetHEdge[x][y] = true;
        }
        else {
            if(isSetVEdge[x][y]) return;
            ret = board.setVEdge(x,y,turn);
            vEdge[x][y].setBackground(Color.BLACK);
            isSetVEdge[x][y] = true;
        }

        for(Point p : ret)
        	if (turn == Board.RED)
                box[p.x][p.y].setBackground(Color.RED);
            else if (turn == Board.BLUE)
                box[p.x][p.y].setBackground(Color.BLUE);
            else
                box[p.x][p.y].setBackground(Color.GREEN);


        redScoreLabel.setText(String.valueOf(board.getRedScore()));
        redScoreLabel.setBackground(Color.DARK_GRAY);
        blueScoreLabel.setText(String.valueOf(board.getBlueScore()));
        blueScoreLabel.setBackground(Color.DARK_GRAY);
        greenScoreLabel.setText(String.valueOf(board.getGreenScore()));
        greenScoreLabel.setBackground(Color.DARK_GRAY);

        if(board.isComplete()) {
            int winner = board.getWinner();
            if(winner == Board.RED) {
                statusLabel.setText("Player-1 is the winner!");
                statusLabel.setForeground(Color.RED);
            }
            else if(winner == Board.BLUE) {
                statusLabel.setText("Player-2 is the winner!");
                statusLabel.setForeground(Color.BLUE);
            }
            else if(winner == Board.GREEN) {
                statusLabel.setText("Player-3 is the winner!");
                statusLabel.setForeground(Color.GREEN);
            }
            else {
                statusLabel.setText("Game Tied!");
                statusLabel.setForeground(Color.BLACK);
            }
        }

        if(ret.isEmpty()) {
            if(turn == Board.RED) {
                turn = Board.BLUE;
                solver = blueSolver;
                statusLabel.setText("Player-2's Turn...");
                statusLabel.setForeground(Color.BLUE);
            }
            else if(turn == Board.BLUE) {
                turn = Board.GREEN;
                solver = greenSolver;
                statusLabel.setText("Player-3's Turn...");
                statusLabel.setForeground(Color.GREEN);
            }
            else {
                turn = Board.RED;
                solver = redSolver;
                statusLabel.setText("Player-1's Turn...");
                statusLabel.setForeground(Color.RED);
            }
        }

    }

    private void manageGame() {
        while(!board.isComplete()) {
            if(goBack) return;
            if(solver == null) {
                mouseEnabled = true;
            }
            else {
                mouseEnabled = false;
                processMove(solver.getNextMove(board, turn));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Edge getSource(Object object) {
        for(int i=0; i<(n-1); i++)
            for(int j=0; j<n; j++)
                if(hEdge[i][j] == object)
                    return new Edge(i,j,true);
        for(int i=0; i<n; i++)
            for(int j=0; j<(n-1); j++)
                if(vEdge[i][j] == object)
                    return new Edge(i,j,false);
        return new Edge();
    }

    private JLabel getHorizontalEdge() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(dist, size));
        label.setOpaque(true);
        label.addMouseListener(mouseListener);
        return label;
    }

    private JLabel getVerticalEdge() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(size, dist));
        label.setOpaque(true);
        label.addMouseListener(mouseListener);
        return label;
    }

    private JLabel getDot() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(size, size));
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        return label;
    }

    private JLabel getBox() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(dist, dist));
        label.setOpaque(true);
        return label;
    }

    private JLabel getEmptyLabel(Dimension d) {
        JLabel label = new JLabel();
        label.setPreferredSize(d);
        return label;
    }

    public GamePlay(Main parent, JFrame frame, int n, GameSolver redSolver, GameSolver blueSolver, GameSolver greenSolver, String redName, String blueName, String greenName) {
        this.parent = parent;
        this.frame = frame;
        this.n = n;
        this.redSolver = redSolver;
        this.blueSolver = blueSolver;
        this.greenSolver = greenSolver;
        this.redName = redName;
        this.blueName = blueName;
        this.greenName = greenName;
        initGame();
    }

    private boolean goBack;

    private ActionListener backListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            goBack = true;
        }
    };

    private void initGame() {

        board = new Board(n);
        int boardWidth = n * size + (n-1) * dist;
        turn = Board.RED;
        solver = redSolver;

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        grid.add(getEmptyLabel(new Dimension(2 * boardWidth, 10)), constraints);
        grid.setBackground(Color.DARK_GRAY);
        
        JPanel playerPanel = new JPanel(new GridLayout(2, 2));
        if(n>3) playerPanel.setPreferredSize(new Dimension(2 * boardWidth, dist));
        else playerPanel.setPreferredSize(new Dimension(2 * boardWidth, 2 * dist));
        playerPanel.add(new JLabel("<html><font color='red'>Player-1:", SwingConstants.CENTER));
        playerPanel.add(new JLabel("<html><font color='blue'>Player-2:", SwingConstants.CENTER));
        playerPanel.add(new JLabel("<html><font color='green'>Player-3:", SwingConstants.CENTER));
        playerPanel.add(new JLabel("<html><font color='red'>" + redName, SwingConstants.CENTER));
        playerPanel.add(new JLabel("<html><font color='blue'>" + blueName, SwingConstants.CENTER));
        playerPanel.add(new JLabel("<html><font color='green'>" + greenName, SwingConstants.CENTER));
        playerPanel.setBackground(Color.DARK_GRAY);
        ++constraints.gridy;
        grid.add(playerPanel, constraints);

        ++constraints.gridy;
        grid.add(getEmptyLabel(new Dimension(2 * boardWidth, 10)), constraints);

        JPanel scorePanel = new JPanel(new GridLayout(2, 2));
        scorePanel.setPreferredSize(new Dimension(2 * boardWidth, dist));
        scorePanel.add(new JLabel("<html><font color='red'>Score:", SwingConstants.CENTER));
        scorePanel.add(new JLabel("<html><font color='blue'>Score:", SwingConstants.CENTER));
        scorePanel.add(new JLabel("<html><font color='green'>Score:", SwingConstants.CENTER));
        redScoreLabel = new JLabel("0", SwingConstants.CENTER);
        redScoreLabel.setForeground(Color.RED);
        scorePanel.add(redScoreLabel);
        blueScoreLabel = new JLabel("0", SwingConstants.CENTER);
        blueScoreLabel.setForeground(Color.BLUE);
        scorePanel.add(blueScoreLabel);
        greenScoreLabel = new JLabel("0", SwingConstants.CENTER);
        greenScoreLabel.setForeground(Color.GREEN);
        scorePanel.add(greenScoreLabel);
        scorePanel.setBackground(Color.DARK_GRAY);
        ++constraints.gridy;
        grid.add(scorePanel, constraints);

        ++constraints.gridy;
        grid.add(getEmptyLabel(new Dimension(2 * boardWidth, 10)), constraints);
        grid.setBackground(Color.DARK_GRAY);

        hEdge = new JLabel[n-1][n];
        isSetHEdge = new boolean[n-1][n];

        vEdge = new JLabel[n][n-1];
        isSetVEdge = new boolean[n][n-1];

        box = new JLabel[n-1][n-1];

        for(int i=0; i<(2*n-1); i++) {
            JPanel pane = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
            if(i%2==0) {
                pane.add(getDot());
                for(int j=0; j<(n-1); j++) {
                    hEdge[j][i/2] = getHorizontalEdge();
                    hEdge[j][i/2].setBackground(Color.GRAY);
                    pane.add(hEdge[j][i/2]);
                    pane.add(getDot());
                }
            }
            else {
                for(int j=0; j<(n-1); j++) {
                    vEdge[j][i/2] = getVerticalEdge();
                    vEdge[j][i/2].setBackground(Color.GRAY);
                    pane.add(vEdge[j][i/2]);
                    box[j][i/2] = getBox();
                    box[j][i/2].setBackground(Color.DARK_GRAY);
                    pane.add(box[j][i/2]);
                }
                vEdge[n-1][i/2] = getVerticalEdge();
                vEdge[n-1][i/2].setBackground(Color.GRAY);
                pane.add(vEdge[n-1][i/2]);
            }
            ++constraints.gridy;
            pane.setBackground(Color.DARK_GRAY);
            grid.add(pane, constraints);
        }

        ++constraints.gridy;
        grid.add(getEmptyLabel(new Dimension(2 * boardWidth, 10)), constraints);

        statusLabel = new JLabel("Player-1's Turn...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setPreferredSize(new Dimension(2 * boardWidth, dist));
        ++constraints.gridy;
        grid.add(statusLabel, constraints);

        ++constraints.gridy;
        grid.add(getEmptyLabel(new Dimension(2 * boardWidth, 10)), constraints);

        JButton goBackButton = new JButton("Go Back to Main Menu");
        goBackButton.setPreferredSize(new Dimension(boardWidth, dist));
        goBackButton.addActionListener(backListener);
        ++constraints.gridy;
        grid.add(goBackButton, constraints);

        frame.getContentPane().removeAll();
        frame.revalidate();
        
        frame.repaint();

        frame.setContentPane(grid);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.DARK_GRAY );

        goBack = false;
        manageGame();

        while(!goBack) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        parent.initGUI();
    }

}