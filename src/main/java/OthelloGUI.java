import processing.core.PApplet;
import processing.core.PGraphics;


public class OthelloGUI extends PApplet {
    public static void main(String[] args) { PApplet.runSketch(new String[]{""}, new OthelloGUI()); }
    int[] board = new int[64];
    OthelloInterface o = Othello.of(board);
    Box[] box = new Box[board.length];
    Button play, newGame, undo, pvp, pvc, mainmenu;
    int screen = 0;
    int player = -1;
    int choose = 0;
    int vsComp;

    final int xpos = 20;
    final int ypos = 200;
    final int xoffset = 20;
    final int yoffset = 20;
    final int tilesize = 50;
    final int bordersize = 5;

    public void setup() {
        o = o.newGame();
        createBoard();
        play = new Button(600, 100);
        newGame = new Button(580, 600);
        undo = new Button(580, 270);
        pvp = new Button(400, 130);
        pvc = new Button(300, 130);
        mainmenu = new Button(580, 170);
    }

    public void settings() {
        size(700, 700);
    }

    public void createBoard() {
        int edge_length = (int)(sqrt(o.getBoard().length));
        int i = 0;
        int X, Y;
        for (int y = 0; y < edge_length; y++) {
            Y = ypos + yoffset + bordersize + y * (tilesize + bordersize);
            for (int x = 0; x < edge_length; x++) {
                X = xpos + xoffset + bordersize + x * (tilesize + bordersize);
                box[i] = new Box(X, Y, (o.getBoard()[i]));
                i++;
            }
        }
    }

    public void pvp() {
        if (undo.selected) {
            if (o.getHistorySize() <= 1) {
                fill(1);
                textSize(20);
                text("nothing to undo!", 360, 200);
            } else {
                o = o.undo();
                player = -player;
                createBoard();
                undo.selected = false;
            }
        }

        if (player == -1) {
            fill(104, 73, 94);
            rect(75, 75, 75, 50, 15);
            fill(255);
            textSize(27);
            stroke(0);
            text("P1", 110, 110);
            fill(0);
            ellipse(110, 170, 40, 40);
        }
        else {
            fill(0, 116, 153);
            rect(180, 75, 75, 50, 15);
            fill(255);
            textSize(27);
            stroke(0);
            text("P2", 215, 110);
            fill(255);
            noStroke();
            ellipse(215, 170, 40, 40);
        }

        if (o.generateValidMove(player).isEmpty()) {
            if (o.generateValidMove(-player).isEmpty()) screen = 2;
            player = -player;
        }

        for (int i = 0; i < o.getBoard().length; i++) {
            if (box[i].selected) {
                int row = i / 8;
                int col = i % 8;
                if (o.isValid(Move.of(row, col, player), false)) {
                    o = o.play(Move.of(row, col, player));
                    println("Move: " + Move.of(row, col, player).toString());
                    println(o.toString());
                    createBoard();
                    player = -player;
                    box[i].selected = false;
                } else {
                    box[i].selected = true;
                    fill(0);
                    textSize(23);
                    text("invalid move!", 360, 200);
                }
            }
        }
    }

    public void pvc() {

        if (undo.selected) {
            if (o.getHistorySize() <= 1) {
                fill(1);
                textSize(20);
                text("nothing to undo!", 360, 200);
            } else {
                o = o.undo();
                o = o.undo();
                createBoard();
                undo.selected = false;
            }
        }

        if (player == -1) {
            fill(104, 73, 94);
            rect(75, 75, 75, 50, 15);
            fill(255);
            textSize(27);
            stroke(0);
            text("P1", 110, 110);
            fill(0);
            ellipse(110, 170, 40, 40);
        }
        else {
            fill(0, 116, 153);
            rect(180, 75, 75, 50, 15);
            fill(255);
            textSize(27);
            stroke(0);
            text("COMP", 215, 110);
            fill(255);
            noStroke();
            ellipse(215, 170, 40, 40);
        }

        int hasPlayed = 0;

        if (o.generateValidMove(player).isEmpty()) {
            player = -player;
            hasPlayed++;
        }

        for (int i = 0; i < o.getBoard().length; i++) {
            if (box[i].selected) {
                int row = i / 8;
                int col = i % 8;
                if (o.isValid(Move.of(row, col, player), false)) {
                    o = o.play(Move.of(row, col, player));
                    println("Move: " + Move.of(row, col, player).toString());
                    println(o.toString());
                    createBoard();
                    hasPlayed++;
                    box[i].selected = false;
                } else {
                    box[i].selected = true;
                    fill(0);
                    textSize(23);
                    text("invalid move!", 360, 200);
                }
            }
        }

        if (hasPlayed == 1) {
            Move m = o.bestMove();
            o = o.play(m);
            println("Move: " + m.toString());
            println(o.toString());
            createBoard();
        }
    }

    public void draw() {
        if (screen == 0) {
            background(69, 144, 89);
            fill(255);
            textAlign(CENTER);
            textSize(70);
            text("OTHELLO!", width/2, height/2);
            textSize(20);
            text("a strategy board game for two players, played on an 8×8 uncheckered board.", width/2, (height/2) + 40);

            play.mousePress();
            fill(255);
            play.draw(g);
            fill(0);
            textSize(20);
            text("Play!", 600, 107);

            fill(255);
            textSize(20);
            text("choose who you want to fight against", 350, 85);

            pvp.mousePress();
            fill(255);
            pvp.draw(g);
            fill(0);
            textSize(20);
            text("PVP", 400, 137);

            pvc.mousePress();
            fill(255);
            pvc.draw(g);
            fill(0);
            textSize(20);
            text("PVC", 300, 137);

            if (pvp.selected) {
                fill(249, 215, 28);
                pvp.draw(g);
                fill(0);
                textSize(20);
                text("PVP", 400, 137);
                choose = 1;
            }

            if (pvc.selected) {
                fill(249, 215, 28);
                pvc.draw(g);
                fill(0);
                textSize(20);
                text("PVC", 300, 137);
                choose = 2;
            }

            if (play.selected) {
                if (choose == 1) {
                    screen = 1;
                    vsComp = 0;
                    pvp.selected = false;
                } else if (choose == 2) {
                    screen = 1;
                    vsComp = 1;
                    pvc.selected = false;
                }
                play.selected = false;
            }
        }
        else if (screen == 1) {
            background(175, 210, 185);

            fill(0);
            textSize(25);
            text("it's your turn, ", 110, 50);

            mainmenu.mousePress();
            fill(255);
            mainmenu.draw(g);
            fill(0);
            textSize(18);
            text("Main", 580, 165);
            text("Menu", 580, 183);

            newGame.mousePress();
            fill(255);
            newGame.draw(g);
            fill(0);
            textSize(15);
            text("New", 580, 595);
            text("Game!", 580, 610);

            undo.mousePress();
            fill(255);
            undo.draw(g);
            fill(0);
            textSize(20);
            text("UNDO", 580, 277);

            if (mainmenu.selected) {
                screen = 0;
                o = o.newGame();
                createBoard();
                player = -1;
                mainmenu.selected = false;
            }

            if (newGame.selected) {
                o = o.newGame();
                player = -1;
                createBoard();
                newGame.selected = false;
            }

            for (int i = 0; i < ((Othello)o).board.length; i++) {
                if (((Othello) o).board[i] == 0) {
                    box[i].mousePress();
                }
                box[i].draw(g);
            }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (o.isValid(Move.of(i, j, player), false)) {
                        box[i * 8 + j].validBox();
                    }
                }
            }

            fill(0);
            textSize(20);
            text("Scoreboard", 580, 370);
            text("----------------", 580, 380);
            fill(104, 73, 94);
            rect(530, 390, 100, 40);
            fill(0, 116, 153);
            rect(530, 440, 100, 40);
            fill(255);
            textSize(22);
            text("P1 = " + o.getScore()[0], 580, 420);
            text("P2 = " + o.getScore()[1], 580, 470);

            if (vsComp == 1) {
                pvc();
            } else pvp();

            if(o.isGameOver()) screen = 2;
        }
        else if (screen == 2) {
            background(59, 100, 70);
            fill(255);
            textAlign(CENTER);
            textSize(70);
            text("GAME OVER", width/2, height/2);

            textSize(40);
            if (o.getWinner() == -1) {
                text("player 1 wins!", width/2, height/2 - 80);
            } else if (o.getWinner() == 1) {
                text("player 2 wins!", width/2, height/2 - 80);
            } else if (o.getWinner() == 0) {
                text("tie game!", width/2, height/2 - 80);
            }

            textSize(25);
            text("right click anywhere to play again", width/2, (height/2) + 40);

            if (mousePressed && mouseButton == RIGHT) {
                screen = 0;
                setup();
            }
        }
    }

    class Box {
        int x, y;
        int size = 50;
        boolean pressed = false;
        boolean selected = false;
        int boxColor = color(63, 104, 76);
        int player;

        Box(int x, int y, int player) {
            this.x = x;
            this.y = y;
            this.player = player;
        }

        void draw (PGraphics g) {
            g.noStroke();
            g.fill(boxColor);
            g.rect(x, y, size, size, 15);
            if(player == 1) {
                g.fill(255);
            } else if(player == -1) {
                g.fill(0);
            }
            g.ellipse(x + size/2 + 1, y + size/2 + 1, 25, 25);
        }

        void validBox() {
            boxColor = color(221, 255, 234);
        }

        void mousePress() {
            if (mousePressed && mouseButton == LEFT && !pressed) {
                pressed = true;
                if (mouseX >= x && mouseX <= x + 50 && mouseY >= y && mouseY <= y + 50 && !selected) {
                    selected = true;
                    boxColor = color(249, 215, 28);
                }
                else if (mouseX >= x && mouseX <= x + 50 && mouseY >= y && mouseY <= y + 50 && selected) {
                    selected = false;
                    boxColor = color(63, 104, 76);
                }
            }
            if (!mousePressed) {
                pressed = false;
            }
        }
    }

    class Button {
        boolean pressed = false;
        boolean selected = false;
        int x;
        int y;
        int r = 60;

        Button(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void draw(PGraphics g) {
            g.ellipse(x, y, r, r);
        }

        void mousePress() {
            if (mousePressed && mouseButton == LEFT && !pressed) {
                pressed = true;
                selected = mouseX >= x - 30 && mouseX <= x + 30 && mouseY >= y - 30 && mouseY <= y + 30 && !selected;
            }
            if (!mousePressed) {
                pressed = false;
            }
        }
    }
}
