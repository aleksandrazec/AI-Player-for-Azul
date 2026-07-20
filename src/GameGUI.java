import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    int selectedTileType=-1;
//    0-4 are factories, 5 is center
    int selectedTileFactory=-1;
    Game game;
    JPanel panel;
    JPanel gameEnvironment;
    List<Tile>[] factories;
    JPanel[] factoryPanels;
    JPanel upperFactories;
    JPanel lowerFactories;
    JPanel centerPanel;
    JPanel centerGrid;

    List<Tile> centerOfTable;
    JPanel maxBoard;
    int maxTotalPoints;
    JLabel maxTotalPointsLabel;
    Tile[][] maxWall;
    Tile[][] maxPatternLines;
    Tile[] maxMinusPoints;

    JPanel minBoard;
    int minTotalPoints;
    JLabel minTotalPointsLabel;
    Tile[][] minWall;
    Tile[][] minPatternLines;
    Tile[] minMinusPoints;
    Color background = new Color(219,175,160);
    private final int TILE_SIZE = 48;
    ImageIcon oneTile = scaleImageToTileSize(new ImageIcon("assets/one_tile.png"));
    ImageIcon blackTile = scaleImageToTileSize(new ImageIcon("assets/black_tile.png"));
    ImageIcon blueTile = scaleImageToTileSize(new ImageIcon("assets/blue_tile.png"));
    ImageIcon cyanTile = scaleImageToTileSize(new ImageIcon("assets/cyan_tile.png"));
    ImageIcon redTile = scaleImageToTileSize(new ImageIcon("assets/red_tile.png"));
    ImageIcon yellowTile =  scaleImageToTileSize(new ImageIcon("assets/yellow_tile.png"));
    ImageIcon placeholderTile = scaleImageToTileSize(new ImageIcon("assets/placeholder_tile.png"));
    private final EmptyBorder emptyBorder;

    private ImageIcon scaleImageToTileSize(ImageIcon icon) {
        Image scaledImage = icon.getImage().getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    public ImageIcon getTileImage(int type){
        return switch (type) {
            case 0 -> oneTile;
            case 1 -> blueTile;
            case 2 -> yellowTile;
            case 3 -> redTile;
            case 4 -> blackTile;
            case 5 -> cyanTile;
            default -> placeholderTile;
        };
    }
    public class Tile implements ActionListener {
        protected JLabel label;
        protected JButton button;
        int type;
        boolean isButton;
//        0=factory,1=center,2=pattern line,3=wall,4=floor line
        int category;
//        0-4 factories, 5 center
        int factory=-1;
//        0-4 actual pattern lines, 5 floor line
        int patternLine=-1;
//        0-min, 1-max, 2-unassigned
        int belongsTo;
        public Tile(int type, boolean inactive, boolean isButton, int category, int belongsTo){
            this.belongsTo=belongsTo;
            this.category=category;
            this.isButton=isButton;
            if(isButton){
                this.button=new JButton();
                this.button.setMargin(new Insets(0, 0, 0, 0));
                this.button.setContentAreaFilled(false);
                this.button.setBorderPainted(false);
//                this.button.setFocusPainted(false);
                this.button.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.button.setMinimumSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.button.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.button.addActionListener(this);
            }else {
                this.label = new JLabel();
                this.label.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.label.setMinimumSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.label.setMaximumSize(new Dimension(TILE_SIZE, TILE_SIZE));
                this.label.setHorizontalAlignment(SwingConstants.CENTER);
                this.label.setVerticalAlignment(SwingConstants.CENTER);
            }
            this.type=type;
            if(inactive){
                makeInactive();
            }else{
                makeActive();
            }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!game.maxTurn){
                if(selectedTileType==-1){
                    if(factory!=-1){
                        selectedTileType=type;
                        selectedTileFactory=factory;
//                        System.out.println("Selected "+type+" tile from "+factory+" factory");
                    }
                }else{
                    if(patternLine!=-1 && belongsTo==0){
//                    if(patternLine!=-1){
                        playTurn(selectedTileFactory,selectedTileType,patternLine);
                    }
                    selectedTileFactory=-1;
                    selectedTileType=-1;
                }
            }
        }
        public void setBelongsTo(int belongsTo){this.belongsTo=belongsTo;}
        public void setFactory(int factory){
            this.factory=factory;
        }
        public void setPatternLine(int patternLine){
            this.patternLine=patternLine;
        }
        public void disableButton(){
            button.setEnabled(false);
        }
        public void enableButton(){
            button.setEnabled(true);
        }
        public void setColor(Color color){
            if(isButton){
                button.setIcon(null);
                button.setBackground(color);
            }else{
                label.setIcon(null);
                label.setBackground(color);
            }
        }
        public void makeActive(){
            if(isButton){
                button.setIcon(getTileImage(type));
            }else {
                label.setIcon(getTileImage(type));
            }
        }
        public void makeInactive(){
            ImageIcon icon=getTileImage(type);
            final int w = icon.getIconWidth();
            final int h = icon.getIconHeight();
            GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g2d = image.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            BufferedImage gray = applyPartialGray(image, 0.7f);
//            Image gray = GrayFilter.createDisabledImage(image);
            if(isButton){
                button.setIcon(new ImageIcon(gray));
            }else {
                label.setIcon(new ImageIcon(gray));
            }
        }
        private BufferedImage applyPartialGray(BufferedImage original, float grayFactor) {
            int w = original.getWidth();
            int h = original.getHeight();
            BufferedImage result = new BufferedImage(w, h, original.getType());

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int rgb = original.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    int gray = (int)(0.3 * red + 0.59 * green + 0.11 * blue);

                    int newRed = (int)(red * (1 - grayFactor) + gray * grayFactor);
                    int newGreen = (int)(green * (1 - grayFactor) + gray * grayFactor);
                    int newBlue = (int)(blue * (1 - grayFactor) + gray * grayFactor);

                    int newRgb = (newRed << 16) | (newGreen << 8) | newBlue;
                    result.setRGB(x, y, newRgb);
                }
            }
            return result;
        }
        public void makeEmpty(){
            if(isButton){
                button.setIcon(getTileImage(-1));
            }else {
                label.setIcon(getTileImage(-1));
            }
        }
    }
    public void playTurn(int selectedTileFactory, int selectedTileType, int patternLine){
        if(game.playTurn(selectedTileFactory,selectedTileType,patternLine)){
            updateGameState(selectedTileFactory);
        }else{
            JOptionPane.showMessageDialog(null, "This isn't a legal move.", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        this.selectedTileType = -1;
        this.selectedTileFactory = -1;
    }

    private void updateGameState(int selectedTileFactory){
        updateFactories(selectedTileFactory);
        updateCenterOfTable();
        updatePatternLines(true);
        updatePatternLines(false);
        updateWalls(true);
        updateWalls(false);
        updateFloorLines(true);
        updateFloorLines(false);
        updateScores();
        panel.revalidate();
        panel.repaint();
    }

    private void updateFactories(int selectedTileFactory){
        int[][] gameFactories=game.getFactories();
        for (int i = 0; i < 5; i++) {
            JPanel factoryPanel=factoryPanels[i];
            factoryPanel.removeAll();

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill=GridBagConstraints.BOTH;
            gbc.weightx=1.0;
            gbc.weighty=1.0;
            gbc.insets=new Insets(2,2,2,2);

            factories[i].clear();
            int row=0;
            int col=0;

            if(getTotalTiles(gameFactories[i])!=0){

            for (int j = 0; j < gameFactories[i].length; j++) {
                for (int k = 0; k < gameFactories[i][j]; k++) {
                    Tile tile=new Tile(j,false,true,0,2);
                    gbc.gridx=col;
                    gbc.gridy=row;
                    factoryPanel.add(tile.button,gbc);
                    factories[i].add(tile);
                    tile.setFactory(i);

                    col++;
                    if(col>=2){
                        col=0;
                        row++;
                    }
                }
            }
            }else {
                row = 0;
                col = 0;
                for (int j = 0; j < 4; j++) {
                    Tile emptyTile = new Tile(-1, true, true, 0, 2);
                    gbc.gridx = col;
                    gbc.gridy = row;
                    factoryPanel.add(emptyTile.button, gbc);
                    factories[i].add(emptyTile);
                    emptyTile.setFactory(i);

                    col++;
                    if (col >= 2) {
                        col = 0;
                        row++;
                    }
                }

            }
            factoryPanel.revalidate();
            factoryPanel.repaint();
        }
    }
    public void gameOver(int botPoints, int playerPoints) {
        for (List<Tile> factory : factories) {
            for (Tile tile : factory) {
                if (tile.isButton) tile.disableButton();
            }
        }
        for (Tile tile : centerOfTable) {
            if (tile.isButton) tile.disableButton();
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (maxPatternLines[i][j] != null && maxPatternLines[i][j].isButton)
                    maxPatternLines[i][j].disableButton();
                if (minPatternLines[i][j] != null && minPatternLines[i][j].isButton)
                    minPatternLines[i][j].disableButton();
            }
        }
        for (Tile tile : maxMinusPoints) {
            if (tile != null && tile.isButton) tile.disableButton();
        }
        for (Tile tile : minMinusPoints) {
            if (tile != null && tile.isButton) tile.disableButton();
        }

        String winner;
        if (botPoints > playerPoints) {
            winner = "Bot wins!";
        } else if (playerPoints > botPoints) {
            winner = "You win!";
        } else {
            winner = "It's a tie!";
        }
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    winner + "\n\nBot: " + botPoints + " points\nYou: " + playerPoints + " points",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();          // close the game window
            System.exit(0);     // terminate the application
        });

    }
    private void updateCenterOfTable(){
        int[] centerData = game.getCenterOfTable();
        centerGrid.removeAll();
        centerOfTable.clear();

        int tileIndex=0;
        int totalTiles=getTotalTiles(centerData);

        for (int i = 0; i < centerData.length; i++) {
            int count=centerData[i];
            for (int j = 0; j < count && tileIndex<16; j++) {
                Tile tile=new Tile(i, false, true, 1, 2);
                centerGrid.add(tile.button);
                centerOfTable.add(tile);
                tile.setFactory(5);
                tileIndex++;
            }
        }

        while(tileIndex<16){
            Tile emptyTile=new Tile(-1,false,true,1,2);
            emptyTile.makeEmpty();
            centerGrid.add(emptyTile.button);
            centerOfTable.add(emptyTile);
            emptyTile.setFactory(-1);
            tileIndex++;
        }
        centerGrid.revalidate();
        centerGrid.repaint();
    }
    private void updatePatternLines(boolean isMax) {
        Board board = isMax ? game.getMax() : game.getMin();
        Tile[][] patternLines = isMax ? maxPatternLines : minPatternLines;
        int[][] boardPatternLines = board.getPatternLines();

        for (int i = 0; i < 5; i++) {
            int tileType = getTypeFromPatternLine(boardPatternLines[i]);
            int totalTiles = getTotalTiles(boardPatternLines[i]);

            int temp = 0;
            for (int j = 4; j >= 0 && temp<(i+1); j--) {
                if (StaticGameData.patternLinePattern[i][j] != 0 && patternLines[i][j] != null) {
                    Tile tile = patternLines[i][j];
                    if (totalTiles>temp && tileType > 0) {
                        tile.type = tileType;
                        tile.makeActive();
                    } else {
                        tile.type = -1;
                        tile.makeEmpty();
                    }
                    temp++;
                }
            }
        }
    }
    private void updateWalls(boolean isMax){
        Board board=isMax ? game.getMax() : game.getMin();
        Tile[][] wall = isMax ? maxWall : minWall;
        int[][] boardWall = board.getWall();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(boardWall[i][j]==1 && wall[i][j]!=null){
                    wall[i][j].makeActive();
                }else{
                    wall[i][j].makeInactive();
                }
            }
        }
    }
    private void updateFloorLines(boolean isMax){
        Board board = isMax ? game.getMax() : game.getMin();
        Tile[] floorLine = isMax ? maxMinusPoints : minMinusPoints;
        int[] boardFloorLine = board.getFloorLine();

        for (Tile tile: floorLine){
            tile.type=-1;
            tile.makeEmpty();
        }
        int tileIndex=0;
        for (int i = 0; i < boardFloorLine.length; i++) {
            for (int j = 0; j < boardFloorLine[i] && tileIndex<floorLine.length; j++) {
                floorLine[tileIndex].type=i;
                floorLine[tileIndex].makeActive();
                tileIndex++;
            }
        }
    }
    private void updateScores(){
        maxTotalPoints= game.getMax().getCurrentPoints();
        minTotalPoints=game.getMin().getCurrentPoints();
        maxTotalPointsLabel.setText(Integer.toString(maxTotalPoints));
        minTotalPointsLabel.setText(Integer.toString(minTotalPoints));
    }
    private int getTypeFromPatternLine(int[] patternLine) {
        for (int i = 1; i < patternLine.length; i++) {
            if (patternLine[i] > 0) {
                return i;
            }
        }
        return -1;
    }
    private int getTotalTiles(int[] tiles) {
        int total = 0;
        for (int i = 1; i < tiles.length; i++) {
            total += tiles[i];
        }
        return total;
    }


    public GameGUI(Game game,int[][] factories) throws IOException{
        this.game=game;
        this.centerOfTable=new ArrayList<>();
        this.factories=new List[5];
        for (int i = 0; i < 5; i++) {
            this.factories[i]=new ArrayList<>();
        }
        this.factoryPanels= new JPanel[5];
        this.maxWall=new Tile[5][5];
        this.minWall=new Tile[5][5];
        this.maxPatternLines=new Tile[5][5];
        this.minPatternLines=new Tile[5][5];
        this.maxMinusPoints=new Tile[7];
        this.minMinusPoints=new Tile[7];

        this.setTitle("Azul");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(25, 200);
        this.setSize(1500, 500);
        this.setIconImage(getTileImage(5).getImage());
        this.emptyBorder=new EmptyBorder(2,1,5,3);
        panel=createPanel(factories);
        this.add(panel);
        this.setVisible(true);
    }
    public JPanel createPanel(int[][] factories){
        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        maxBoard=createBoard(true);
        minBoard=createBoard(false);
        gameEnvironment=createGameEnvironment(factories);
        main.setBackground(background);

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        main.add(maxBoard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.30;
        main.add(gameEnvironment, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.35;
        main.add(minBoard, gbc);

        return main;
    }
    public JPanel createGameEnvironment(int[][] factories){
        JPanel gameEnvironment = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        upperFactories = new JPanel(new GridBagLayout());
        GridBagConstraints upperGbc = new GridBagConstraints();
        upperGbc.fill = GridBagConstraints.BOTH;
        upperGbc.weightx = 1.0;
        upperGbc.weighty = 1.0;
        upperGbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < 3; i++) {
            upperGbc.gridx = i;
            JPanel factory = createFactory(factories[i], i);
            factoryPanels[i] = factory;
            upperFactories.add(factory, upperGbc);
        }

        lowerFactories = new JPanel(new GridBagLayout());
        GridBagConstraints lowerGbc = new GridBagConstraints();
        lowerGbc.fill = GridBagConstraints.BOTH;
        lowerGbc.weightx = 1.0;
        lowerGbc.weighty = 1.0;
        lowerGbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 3; i < 5; i++) {
            lowerGbc.gridx = (i - 3);
            JPanel factory = createFactory(factories[i], i);
            factoryPanels[i] = factory;
            lowerFactories.add(factory, lowerGbc);
        }


//        because center can have maximum 16 tiles
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Center of Table"));
        centerPanel.setBackground(background);

        centerGrid = new JPanel(new GridLayout(2, 8, 5, 5));
        centerGrid.setBackground(background);
        Tile oneTile=new Tile(0, false,true,1,2);
        centerGrid.add(oneTile.button);
        centerOfTable.add(oneTile);
        oneTile.setFactory(5);
        for (int i = 1; i < 16; i++) {
            Tile tile = new Tile(-1,false, true,1,2);
//            tile.disableButton();
            centerGrid.add(tile.button);
            centerOfTable.add(tile);
            tile.setFactory(5);
        }
        centerPanel.add(centerGrid, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4;
        gameEnvironment.add(upperFactories, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        gameEnvironment.add(centerPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.4;
        gameEnvironment.add(lowerFactories, gbc);

        return gameEnvironment;
    }

    public JPanel createFactory(int[] factory, int factoryIndex){
        JPanel factoryPanel = new JPanel(new GridBagLayout());
        factoryPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Factory " + (factoryIndex + 1)));
        factoryPanel.setBackground(background);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(2, 2, 2, 2);

        int row = 0, col = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < factory[i]; j++) {
                Tile tile = new Tile(i, false, true,0,2);
                gbc.gridx = col;
                gbc.gridy = row;
                factoryPanel.add(tile.button, gbc);
                factories[factoryIndex].add(tile);
                tile.setFactory(factoryIndex);

                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
        }

        return factoryPanel;
    }
    public JPanel createBoard(boolean isMax){
        JPanel board = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        String playerName = isMax ? "Bot" : "You";
        board.setBorder(BorderFactory.createTitledBorder(emptyBorder,playerName));
        board.setBackground(background);

        JPanel topSection = new JPanel(new GridBagLayout());
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.fill = GridBagConstraints.BOTH;
        topGbc.weightx = 1.0;
        topGbc.weighty = 1.0;
        topGbc.insets = new Insets(5, 5, 5, 5);

        JPanel patternLines = new JPanel(new GridBagLayout());
        patternLines.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Pattern Lines"));
        GridBagConstraints patternGbc = new GridBagConstraints();
        patternGbc.fill = GridBagConstraints.BOTH;
        patternGbc.weightx = 1.0;
        patternGbc.weighty = 1.0;

        JPanel patternGrid = new JPanel(new GridLayout(5, 5, 2, 2));
        patternGrid.setBackground(background);

        if(isMax){
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if(StaticGameData.patternLinePattern[i][j]==0){
                        Tile tile = new Tile(-1, false, false,2,1);
                        tile.setColor(background);
                        patternGrid.add(tile.label);
                        maxPatternLines[i][j]=tile;
                    }else{
                        Tile tile = new Tile(-1, false, true,2,1);
                        patternGrid.add(tile.button);
                        tile.setPatternLine(i);
                        maxPatternLines[i][j]=tile;
                    }
                }
            }
        }else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if(StaticGameData.patternLinePattern[i][j]==0){
                        Tile tile = new Tile(-1, false, false,2,0);
                        tile.setColor(background);
                        patternGrid.add(tile.label);
                        minPatternLines[i][j]=tile;
                    }else{
                        Tile tile = new Tile(-1, false, true,2,0);
                        patternGrid.add(tile.button);
                        tile.setPatternLine(i);
                        minPatternLines[i][j]=tile;
                    }
                }
            }
        }
        patternLines.add(patternGrid);

        JPanel wall = new JPanel(new GridBagLayout());
        wall.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Wall"));

        JPanel wallGrid = new JPanel(new GridLayout(5, 5, 2, 2));
        wallGrid.setBackground(background);

        if(isMax) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    Tile tile = new Tile(StaticGameData.wallPattern[i][j], true, false,3,1);
                    wallGrid.add(tile.label);
                    maxWall[i][j] = tile;
                }
            }
        }else{
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    Tile tile = new Tile(StaticGameData.wallPattern[i][j], true, false,3,0);
                    wallGrid.add(tile.label);
                    minWall[i][j] = tile;
                }
            }
        }

        wall.add(wallGrid);

        topGbc.gridx = 0;
        topGbc.weightx = 0.5;
        topSection.add(patternLines, topGbc);

        topGbc.gridx = 1;
        topGbc.weightx = 0.5;
        topSection.add(wall, topGbc);

        JPanel bottomSection = new JPanel(new GridBagLayout());
        GridBagConstraints bottomGbc = new GridBagConstraints();
        bottomGbc.fill = GridBagConstraints.BOTH;
        bottomGbc.insets = new Insets(5, 5, 5, 5);

        JPanel minusPointsPanel = new JPanel(new BorderLayout());
        minusPointsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Floor Line"));
        minusPointsPanel.setBackground(background);

        JPanel minusPointsValues = new JPanel(new GridLayout(1, 7, 5, 5));
        minusPointsValues.setBackground(background);
        for (int i = 0; i < 7; i++) {
            JLabel minusPointsLabel = new JLabel(Integer.toString(StaticGameData.floorLineValues[i]), SwingConstants.CENTER);
            minusPointsValues.add(minusPointsLabel);
        }
        JPanel minusPointsTiles = new JPanel(new GridLayout(1, 7, 5, 5));
        minusPointsTiles.setBackground(background);

        if(isMax){
            for (int i = 0; i < 7; i++) {
                Tile tile=new Tile(-1, false, true,4,1);
                minusPointsTiles.add(tile.button);
                maxMinusPoints[i]=tile;
                tile.setPatternLine(5);
            }
        }else{
            for (int i = 0; i < 7; i++) {
                Tile tile=new Tile(-1, false, true,4,0);
                minusPointsTiles.add(tile.button);
                minMinusPoints[i]=tile;
                tile.setPatternLine(5);
            }
        }
        minusPointsPanel.add(minusPointsValues, BorderLayout.NORTH);
        minusPointsPanel.add(minusPointsTiles, BorderLayout.CENTER);


        JPanel totalPointsPanel = new JPanel(new GridBagLayout());
        totalPointsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Score"));
        totalPointsPanel.setBackground(background);
        GridBagConstraints totalGbc = new GridBagConstraints();
        totalGbc.fill = GridBagConstraints.BOTH;
        totalGbc.insets = new Insets(10, 10, 10, 10);

        JLabel totalPointsLabelText = new JLabel("Total Points: ", SwingConstants.CENTER);
        if(isMax){
            maxTotalPointsLabel = new JLabel(Integer.toString(this.maxTotalPoints), SwingConstants.CENTER);
            totalGbc.gridx = 0;
            totalGbc.gridy = 0;
            totalGbc.weightx = 1.0;
            totalPointsPanel.add(totalPointsLabelText, totalGbc);

            totalGbc.gridy = 1;
            totalGbc.weighty = 1.0;
            totalPointsPanel.add(maxTotalPointsLabel, totalGbc);
        }else{
            minTotalPointsLabel = new JLabel(Integer.toString(this.minTotalPoints), SwingConstants.CENTER);
            totalGbc.gridx = 0;
            totalGbc.gridy = 0;
            totalGbc.weightx = 1.0;
            totalPointsPanel.add(totalPointsLabelText, totalGbc);

            totalGbc.gridy = 1;
            totalGbc.weighty = 1.0;
            totalPointsPanel.add(minTotalPointsLabel, totalGbc);
        }

        bottomGbc.gridx = 0;
        bottomGbc.weightx = 0.7;
        bottomSection.add(minusPointsPanel, bottomGbc);

        bottomGbc.gridx = 1;
        bottomGbc.weightx = 0.3;
        bottomSection.add(totalPointsPanel, bottomGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        board.add(topSection, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.3;
        board.add(bottomSection, gbc);

        return board;
    }
}