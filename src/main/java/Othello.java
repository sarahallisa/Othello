import java.util.*;
class Move {
    final int row, col;
    final int player;
    static Move of(int row, int col, int player) { return new Move(row, col, player); }
    private Move(int row, int col, int player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }
    public String toString() {
        return "(" + row + ", " + col + ", " + player + ")";
    }
}
interface OthelloInterface {
    OthelloInterface play(Move... moves);
    OthelloInterface newGame();
    boolean isValid(Move m, boolean save);
    Move bestMove();
    OthelloInterface undo();
    boolean isGameOver();
    int[] getScore();
    int[] getBoard();
    default int getWinner() {
        if (getScore()[0] > getScore()[1]) return -1; // p1 wins
        else if (getScore()[0] < getScore()[1]) return 1; // p2 wins
        else return 0; // tie game
    };
    int getHistorySize();
    List<Move> generateValidMove(int player);
}
class Othello implements OthelloInterface {
    int[] board;
    Move m;
    int desiredDepth;
    int player;
    static int debug = 0;
    private Random r = new Random();
    static Stack<Othello> history = new Stack<>();
    List<Integer> checkedList = new ArrayList<>();
    public static Othello of(int... board) { return new Othello(board); }
    private Othello(int... board) {
        assert Arrays.stream(board).allMatch(n -> ((n == 0) || (n == 1) || (n == -1))) : "Das Brett hat ungültige Spielfeld";
        this.board = Arrays.copyOf(board, board.length);
    }
    public Othello newGame() {
        Othello o = this;
        history.clear();
        Arrays.fill(board, 0);
        for (int i = 0; i < board.length; i++) {
            if ((i == 3 * 8 + 3) || (i == 4 * 8 + 4)) board[i] = -1;
            if ((i == 3 * 8 + 4) || (i == 4 * 8 + 3)) board[i] = 1;
        }
        o.player = -1;
        history.push(o);
        return o;
    }
    private Othello play(Move m) {
        Othello o = Othello.of(board);
        assert m.player == 1 || m.player == -1 : "Das Brett hat ungültige Spielstein";
        assert m.row >= 0 && m.row <= 7 : "Das Brett hat entweder weniger oder mehr Reihe als erwartet";
        assert m.col >= 0 && m.col <= 7 : "Das Brett hat entweder weniger oder mehr Spalte als erwartet";
        o.isValid(m, true);
        o.board[m.row * 8 + m.col] = m.player;
        flip(o);
        o.player = -m.player;
        return o;
    }
    public Othello play(Move... moves) {
        Othello o = this;
        for (Move m : moves) {
            o = o.play(m);
            history.push(o);
        }
        return o;
    }
    public Othello undo() {
        Othello o;
        assert !history.stream().allMatch(Objects::isNull) : "Das Spiel muss mindestens ein mal gespielt werden";
        history.pop();
        o = history.stream().reduce((first, second) -> second).orElse(null);
        return o;
    }
    public Move bestMove() {
        desiredDepth = 2;
        minimax(this, 0, true);
        return m;
    }
    public List<Move> generateValidMove(int player) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i * 8 + j] == 0) {
                    if (isValid(Move.of(i, j, player), false)) {
                        moves.add(Move.of(i, j, player));
                    }
                }
            }
        }
        return moves;
    }
    public int playRandomly(Othello o) {
        Othello temp = Othello.of(o.board);
        int value = temp.getWinner();
        while (!temp.isGameOver()) {
            List<Move> validMoves = temp.generateValidMove(player);
            if (validMoves.isEmpty()) return 0;
            m = validMoves.get(r.nextInt(validMoves.size()));
            temp = temp.play(m);
            player = -player;
            value = temp.getWinner();
        }
        return value;
    }
    public int[] simulatePlays(Othello o, int iterations) {
        int[] count = new int[3];
        for (int i = 0; i < iterations; i++) {
            int result = o.playRandomly(o);
            if (result == -player) count[0]++; //verloren
            if (result == 0) count[1]++;  //unentschieden
            if (result == player) count[2]++;  //gewonnen
        }
        if (debug == 1) System.out.println("[simulatePlays]: lost -> " + count[0] + ", tie -> " + count[1] + ", won -> " + count[2]);
        return count;
    }
    public int evaluateMoves(Othello o, int iterations) {
        if (o.isGameOver() && (o.getWinner() == 1)) return 100;
        if (o.isGameOver() && (o.getWinner() == -1)) return -100;
        if (o.isGameOver() && (o.getWinner() == 0)) return 0;
        int[] score = simulatePlays(o, iterations);
        if (debug == 1) System.out.println("[evaluateMoves]: score -> " + (score[2] - score[0]));
        return score[2] - score[0];
    }
    public int minimax(Othello o, int depth, boolean isMaximizing) {
        int bestValue;
        if(depth == desiredDepth || o.isGameOver()) {
            if (debug == 1) System.out.println("[minmax]: simulate monte-carlo");
            return (isMaximizing ? 1 : -1) * evaluateMoves(o, 100);
        }
        if (isMaximizing) {
            bestValue = Integer.MIN_VALUE;
            for (Move move : o.generateValidMove(player)) {
                if (debug == 1) System.out.println("[minimax]: before maximizing, depth -> " + depth + ", max -> " + o);
                Othello temp = o.play(move);
                if (debug == 1) System.out.println("[minimax]: after maximizing, depth -> " + depth + ", max -> " + temp);
                player = -player;
                int value = minimax(temp, depth + 1, false);
                player = -player;
                if (value > bestValue) {
                    bestValue = value;
                    if (debug == 1) System.out.println("[minimax]: new max best score -> " + bestValue);
                    if(depth == 0) {
                        m = move;
                        if (debug == 1) System.out.println("[minimax]: save as best move -> move of player " + m.player + " in row " + m.row + " and column " + m.col);
                    }
                }
            }
            return bestValue;
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move move : o.generateValidMove(player)) {
                if (debug == 1) System.out.println("[minimax]: before minimizing, depth -> " + depth + ", min -> " + o);
                Othello temp = o.play(move);
                if (debug == 1) System.out.println("[minimax]: after minimizing, depth -> " + depth + ", min -> " + temp);
                player = -player;
                int value = minimax(temp, depth + 1, true);
                player = -player;
                if (value < bestValue){
                    bestValue = value;
                    if (debug == 1) System.out.println("[minimax]: new min best score -> " + bestValue);
                }
            }
            return bestValue;
        }
    }
    public boolean isValid(Move m, boolean save) {
        int sum = 0;
        if (board[m.row * 8 + m.col] != 0) return false;
        if (checkLeft(m, save)) sum++;
        if (checkRight(m, save)) sum++;
        if (checkTop(m, save)) sum++;
        if (checkTopRight(m, save)) sum++;
        if (checkTopLeft(m, save)) sum++;
        if (checkBottom(m, save)) sum++;
        if (checkBottomRight(m, save)) sum++;
        if (checkBottomLeft(m, save)) sum++;
        return sum != 0;
    }
    public void flip(Othello o) {
        o.checkedList.forEach((n) -> o.board[n] = -o.board[n]);
        o.checkedList.clear();
    }
    private boolean checkLeft(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos - 1 < 0) || (board[pos - 1] == 0)) return false;
        if (m.col > 0) {
            if (board[pos - 1] == -m.player) {
                while (pos - 1 >= 0 && (pos - 1) / 8 == m.row && board[pos - 1] != 0) {
                    if (board[pos - 1] == m.player) return true;
                    if (save) {
                        checkedList.add(pos - 1);
                        addedToList++;
                    }
                    pos--;
                }
            }
        } else if (checkLeft(Move.of(m.row, m.col - 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkRight(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos + 1 > 63) || (board[pos + 1] == 0)) return false;
        if (m.col < 7) {
            if (board[pos + 1] == -m.player) {
                while (pos + 1 <= 63 && (pos + 1) / 8 == m.row && board[pos + 1] != 0) {
                    if (board[pos + 1] == m.player) return true;
                    if (save) {
                        checkedList.add(pos + 1);
                        addedToList++;
                    }
                    pos++;
                }
            }
        } else if(checkRight(Move.of(m.row, m.col + 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkTop(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos - 8 < 0) || (board[pos - 8] == 0)) return false;
        if (m.row > 0) {
            if (board[pos - 8] == -m.player) {
                while (pos - 8 >= 0 && board[pos - 8] != 0) {
                    if (board[pos - 8] == m.player) return true;
                    if (save) {
                        checkedList.add(pos - 8);
                        addedToList++;
                    }
                    pos = pos - 8;
                }
            }
        } else if (checkTop(Move.of(m.row - 1, m.col, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkTopRight(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos - 7 < 0) || (board[pos - 7] == 0)) return false;
        if (m.row > 0 && m.col < 7) {
            if (board[pos - 7] == -m.player) {
                while (pos - 7 >= 0 && board[pos - 7] != 0) {
                    if (board[pos - 7] == m.player) return true;
                    else if ((pos - 7) % 8 == 7) return false;
                    if (save) {
                        checkedList.add(pos - 7);
                        addedToList++;
                    }
                    pos = pos - 7;
                }
            }
        } else if (checkTopRight(Move.of(m.row - 1, m.col + 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkTopLeft(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos - 9 < 0) || (board[pos - 9] == 0)) return false;
        if (m.row > 0 && m.col > 0) {
            if (board[pos - 9] == -m.player) {
                while (pos - 9 >= 0 && board[pos - 9] != 0) {
                    if (board[pos - 9] == m.player) return true;
                    else if ((pos - 9) % 8 == 0) return false;
                    if (save) {
                        checkedList.add(pos - 9);
                        addedToList++;
                    }
                    pos = pos - 9;
                }
            }
        } else if (checkTopLeft(Move.of(m.row - 1, m.col - 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkBottom(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos + 8 > 63) || (board[pos + 8] == 0)) return false;
        if (m.row < 7) {
            if (board[pos + 8] == -m.player) {
                while (pos + 8 <= 63 && board[pos + 8] != 0) {
                    if (board[pos + 8] == m.player) return true;
                    if (save) {
                        checkedList.add(pos + 8);
                        addedToList++;
                    }
                    pos = pos + 8;
                }
            }
        } else if (checkBottom(Move.of(m.row + 1, m.col, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkBottomRight(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos + 9 > 63) || (board[pos + 9] == 0)) return false;
        if (m.row < 7 && m.col < 7) {
            if (board[pos + 9] == -m.player) {
                while (pos + 9 <= 63 && board[pos + 9] != 0) {
                    if (board[pos + 9] == m.player) return true;
                    else if ((pos + 9) % 8 == 1) return false;
                    if (save) {
                        checkedList.add(pos + 9);
                        addedToList++;
                    }
                    pos = pos + 9;
                }
            }
        } else if (checkBottomRight(Move.of(m.row + 1, m.col + 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    private boolean checkBottomLeft(Move m, boolean save) {
        int addedToList = 0;
        int pos = m.row * 8 + m.col;
        if ((pos + 7 > 63) || (board[pos + 7] == 0)) return false;
        if (m.row < 7 && m.col > 0) {
            if (board[pos + 7] == -m.player) {
                while (pos + 7 <= 63 && board[pos + 7] != 0) {
                    if (board[pos + 7] == m.player) return true;
                    else if ((pos + 7) % 8 == 0) return false;
                    if (save) {
                        checkedList.add(pos + 7);
                        addedToList++;
                    }
                    pos = pos + 7;
                }
            }
        } else if(checkBottomLeft(Move.of(m.row + 1, m.col - 1, m.player), save)) return true;
        for (int i = 0; i < addedToList; i++) {
            checkedList.remove(checkedList.size() - 1);
        }
        return false;
    }
    public boolean isGameOver() { return Arrays.stream(board).allMatch(n -> n != 0); }
    public int[] getScore() {
        int[] score = new int[2];
        for (int i : board) {
            if (i == -1) score[0]++;
            else if (i == 1) score[1]++;
        }
        return score;
    }
    public int[] getBoard() { return board; }
    public int getHistorySize() { return history.size(); }
    public String toString() {
        String s = "\n";
        for (int i = 0; i < board.length; i++) {
            s += board[i] + "\t";
            if ((i + 1) % 8 == 0) s += "\n";
        }
        return s;
    }
}