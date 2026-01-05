package controller;

import java.util.ArrayList;
import java.lang.Math;

import view.TileManager;
import model.Entity;

public class Pathfinding {
    TileManager tileM;
    GamePanel gp;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node start, goal, current;
    private boolean goalReached = false;

    public Pathfinding(GamePanel gp) {
        this.gp = gp;
        tileM = TileManager.getInstance();
        createNodes();
    }

    private void createNodes() {
        if (node != null && node.length == gp.mapM.getMap().maxWorldCol
                && node[0].length == gp.mapM.getMap().maxWorldRow) {
            return;
        }

        node = new Node[gp.mapM.getMap().maxWorldCol][gp.mapM.getMap().maxWorldRow];
        int col = 0;
        int row = 0;
        while (col < gp.mapM.getMap().maxWorldCol && row < gp.mapM.getMap().maxWorldRow) {
            node[col][row] = new Node(col, row);
            col++;
            if (col == gp.mapM.getMap().maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    private void resetNodes() {
        createNodes();

        for (int col = 0; col < gp.mapM.getMap().maxWorldCol; col++) {
            for (int row = 0; row < gp.mapM.getMap().maxWorldRow; row++) {
                node[col][row].open = false;
                node[col][row].blocked = false;
                node[col][row].checked = false;
                node[col][row].parent = null;
            }
        }

        openList.clear();
        pathList.clear();
        goalReached = false;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        if (goalCol > gp.mapM.getMap().maxWorldCol || goalCol < 0 || goalRow > gp.mapM.getMap().maxWorldRow
                || goalRow < 0) {
            return;
        }

        resetNodes();
        start = node[startCol][startRow];
        current = start;
        goal = node[goalCol][goalRow];

        openList.add(current);

        int col = 0;
        int row = 0;

        while (col < gp.mapM.getMap().maxWorldCol && row < gp.mapM.getMap().maxWorldRow) {
            int tileNum = gp.mapM.getMap().tileMap[row][col];
            // Check tiles for collision. If yes, change blocked bool to true
            if (tileM.checkTileCollision(tileNum)) {
                node[col][row].blocked = true;
            }

            getCost(node[col][row]);
            col++;

            if (col == gp.mapM.getMap().maxWorldCol) {
                col = 0;
                row++;
            }
        }
    }

    private void getCost(Node node) {
        int xDist = Math.abs(node.col - start.col);
        int yDist = Math.abs(node.row - start.row);
        node.gCost = xDist + yDist;

        xDist = Math.abs(node.col - goal.col);
        yDist = Math.abs(node.row - goal.row);
        node.hCost = xDist + yDist;

        node.fCost = node.gCost + node.hCost;
    }

    public boolean search() {
        while (!goalReached) {
            int col = current.col;
            int row = current.row;

            current.checked = true;
            openList.remove(current);
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }
            if (row + 1 < gp.mapM.getMap().maxWorldRow) {
                openNode(node[col][row + 1]);
            }
            if (col + 1 < gp.mapM.getMap().maxWorldCol) {
                openNode(node[col + 1][row]);
            }
            int bestNodeIndex = 0;
            int bestNodeFCost = 999;
            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).fCost < bestNodeFCost) {
                    bestNodeIndex = i;
                    bestNodeFCost = openList.get(i).fCost;
                } else if (openList.get(i).fCost == bestNodeFCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }
            if (openList.size() == 0) {
                break;
            }

            current = openList.get(bestNodeIndex);
            if (current == goal) {
                goalReached = true;
                pathTracker();
            }
        }

        return goalReached;
    }

    private void openNode(Node node) {
        if (!node.open && !node.checked && !node.blocked) {
            node.open = true;
            node.parent = current;
            openList.add(node);
        }
    }

    private void pathTracker() {
        Node currentNode = goal;

        while (currentNode != start) {
            pathList.add(0, currentNode);
            currentNode = currentNode.parent;
        }
    }

    public void searchPath(int goalCol, int goalRow, Entity entity) {
        int currCol = (entity.worldX + entity.hitbox.x) / gp.tileSize;
        int currRow = (entity.worldY + entity.hitbox.y) / gp.tileSize;

        gp.pathFinder.setNodes(currCol, currRow, goalCol, goalRow);
        boolean goalReached = gp.pathFinder.search();

        if (goalReached && !gp.pathFinder.pathList.isEmpty()) {
            Node firstNode = gp.pathFinder.pathList.get(0);
            int nextX = firstNode.col * gp.tileSize;
            int nextY = firstNode.row * gp.tileSize;
            int farmerLeftX = entity.worldX + entity.hitbox.x;
            int farmerRightX = entity.worldX + entity.hitbox.x + entity.hitbox.width;
            int farmerTopY = entity.worldY + entity.hitbox.y;
            int farmerBotY = entity.worldY + entity.hitbox.y + entity.hitbox.height;
            if (farmerTopY > nextY && farmerLeftX >= nextX && farmerRightX < nextX + gp.tileSize) {
                entity.direction = "up";
            } else if (farmerTopY < nextY && farmerLeftX >= nextX && farmerRightX < nextX + gp.tileSize) {
                entity.direction = "down";
            } else if (farmerTopY >= nextY && farmerBotY < nextY + gp.tileSize) {
                if (farmerLeftX > nextX) {
                    entity.direction = "left";
                }
                if (farmerLeftX < nextX) {
                    entity.direction = "right";
                }

            } else if (farmerTopY > nextY && farmerLeftX > nextX) {
                entity.direction = "up";

                gp.checker.checkTileCollision(entity);

                if (entity.collisionOn) {
                    entity.direction = "left";
                }
            } else if (farmerTopY > nextY && farmerLeftX < nextX) {
                entity.direction = "up";

                gp.checker.checkTileCollision(entity);

                if (entity.collisionOn) {
                    entity.direction = "right";
                }
            } else if (farmerTopY < nextY && farmerLeftX > nextX) {
                entity.direction = "down";

                gp.checker.checkTileCollision(entity);

                if (entity.collisionOn) {
                    entity.direction = "left";
                }

            } else if (farmerTopY < nextY && farmerLeftX < nextX) {
                entity.direction = "down";

                gp.checker.checkTileCollision(entity);

                if (entity.collisionOn) {
                    entity.direction = "right";
                }
            }
        }
    }
}
