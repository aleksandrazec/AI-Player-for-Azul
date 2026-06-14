import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    JPanel panel;
    JPanel gameEnvironment;
    List<Tile>[] factories;
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
    private EmptyBorder emptyBorder;

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
    public class Tile{
        protected JLabel label;
        protected JButton button;
        int type;
        boolean isButton;
        public Tile(int type, boolean inactive, boolean isButton){
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
            BufferedImage gray = applyPartialGray(image, 0.6f);
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
    public GameGUI(int[][] factories) throws IOException{
        this.centerOfTable=new ArrayList<>();
        this.factories=new List[5];
        for (int i = 0; i < 5; i++) {
            this.factories[i]=new ArrayList<>();
        }
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

        JPanel upperFactories = new JPanel(new GridBagLayout());
        GridBagConstraints upperGbc = new GridBagConstraints();
        upperGbc.fill = GridBagConstraints.BOTH;
        upperGbc.weightx = 1.0;
        upperGbc.weighty = 1.0;
        upperGbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < 3; i++) {
            upperGbc.gridx = i;
            JPanel factory = createFactory(factories[i], i);
            upperFactories.add(factory, upperGbc);
        }

        JPanel lowerFactories = new JPanel(new GridBagLayout());
        GridBagConstraints lowerGbc = new GridBagConstraints();
        lowerGbc.fill = GridBagConstraints.BOTH;
        lowerGbc.weightx = 1.0;
        lowerGbc.weighty = 1.0;
        lowerGbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 3; i < 5; i++) {
            lowerGbc.gridx = (i - 3);
            JPanel factory = createFactory(factories[i], i);
            lowerFactories.add(factory, lowerGbc);
        }


//        because center can have maximum 16 tiles
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder(emptyBorder,"Center of Table"));
        center.setBackground(background);

        JPanel centerGrid = new JPanel(new GridLayout(2, 8, 5, 5));
        centerGrid.setBackground(background);
        Tile oneTile=new Tile(0, false,true);
        centerGrid.add(oneTile.button);
        centerOfTable.add(oneTile);
        for (int i = 1; i < 16; i++) {
            Tile tile = new Tile(-1,false, true);
//            tile.disableButton();
            centerGrid.add(tile.button);
            centerOfTable.add(tile);
        }
        center.add(centerGrid, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4;
        gameEnvironment.add(upperFactories, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        gameEnvironment.add(center, gbc);

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
                Tile tile = new Tile(i, false, true);
                gbc.gridx = col;
                gbc.gridy = row;
                factoryPanel.add(tile.button, gbc);
                factories[factoryIndex].add(tile);

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

        String playerName = isMax ? "You" : "Bot";
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
                        Tile tile = new Tile(-1, false, false);
                        tile.setColor(background);
                        patternGrid.add(tile.label);
                        maxPatternLines[i][j]=tile;
                    }else{
                        Tile tile = new Tile(-1, false, true);
                        patternGrid.add(tile.button);
                        maxPatternLines[i][j]=tile;
                    }
                }
            }
        }else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if(StaticGameData.patternLinePattern[i][j]==0){
                        Tile tile = new Tile(-1, false, false);
                        tile.setColor(background);
                        patternGrid.add(tile.label);
                        minPatternLines[i][j]=tile;
                    }else{
                        Tile tile = new Tile(-1, false, true);
                        patternGrid.add(tile.button);
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
                    Tile tile = new Tile(StaticGameData.wallPattern[i][j], true, false);
                    wallGrid.add(tile.label);
                    maxWall[i][j] = tile;
                }
            }
        }else{
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    Tile tile = new Tile(StaticGameData.wallPattern[i][j], true, false);
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
        for (int i = 0; i < 7; i++) {
            JLabel minusPointsLabel = new JLabel(Integer.toString(StaticGameData.floorLineValues[i]), SwingConstants.CENTER);
            minusPointsPanel.add(minusPointsLabel);
        }
        JPanel minusPointsTiles = new JPanel(new GridLayout(1, 7, 5, 5));
        minusPointsTiles.setBackground(background);

        if(isMax){
            for (int i = 0; i < 7; i++) {
                Tile tile=new Tile(-1, false, false);
                minusPointsTiles.add(tile.label);
                maxMinusPoints[i]=tile;
            }
        }else{
            for (int i = 0; i < 7; i++) {
                Tile tile=new Tile(-1, false, false);
                minusPointsTiles.add(tile.label);
                minMinusPoints[i]=tile;
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