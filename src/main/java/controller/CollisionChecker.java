package controller;

import model.Entity;
import model.GameObject;
import view.TileManager;

public class CollisionChecker {
	TileManager tileM;
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
		tileM = TileManager.getInstance();
	}

	private void checkUpCollision(Entity entity, int etRow, int elCol, int erCol) {
		if (gp.mapM.getMap().isSolid(etRow, elCol) || gp.mapM.getMap().isSolid(etRow, erCol))
			entity.collisionOn = true;
	}

	private void checkDownCollision(Entity entity, int ebRow, int elCol, int erCol) {
		if (gp.mapM.getMap().isSolid(ebRow, elCol) || gp.mapM.getMap().isSolid(ebRow, erCol))
			entity.collisionOn = true;
	}

	private void checkLeftCollision(Entity entity, int etRow, int ebRow, int elCol) {
		if (gp.mapM.getMap().isSolid(etRow, elCol) || gp.mapM.getMap().isSolid(ebRow, elCol))
			entity.collisionOn = true;
	}

	private void checkRightCollision(Entity entity, int etRow, int ebRow, int erCol) {
		if (gp.mapM.getMap().isSolid(etRow, erCol) || gp.mapM.getMap().isSolid(ebRow, erCol))
			entity.collisionOn = true;
	}

	public void checkTileCollision(Entity entity) {
		int elX = entity.worldX + entity.hitbox.x;
		int erX = entity.worldX + entity.hitbox.x + entity.hitbox.width;
		int etY = entity.worldY + entity.hitbox.y;
		int ebY = entity.worldY + entity.hitbox.y + entity.hitbox.height;

		int elCol = elX / gp.tileSize;
		int erCol = erX / gp.tileSize;
		int etRow = etY / gp.tileSize;
		int ebRow = ebY / gp.tileSize;

		// Absolute map boundaries
		int mapWidth = gp.mapM.getMap().maxWorldCol * gp.tileSize;
		int mapHeight = gp.mapM.getMap().maxWorldRow * gp.tileSize;

		switch (entity.direction) {
			case "up" -> {
				int nextY = etY - entity.speed;
				if (nextY < 0) {
					entity.collisionOn = true;
					return;
				}
				checkUpCollision(entity, nextY / gp.tileSize, elCol, erCol);
			}
			case "down" -> {
				int nextY = ebY + entity.speed;
				if (nextY >= mapHeight) {
					entity.collisionOn = true;
					return;
				}
				checkDownCollision(entity, nextY / gp.tileSize, elCol, erCol);
			}
			case "left" -> {
				int nextX = elX - entity.speed;
				if (nextX < 0) {
					entity.collisionOn = true;
					return;
				}
				checkLeftCollision(entity, etRow, ebRow, nextX / gp.tileSize);
			}
			case "right" -> {
				int nextX = erX + entity.speed;
				if (nextX >= mapWidth) {
					entity.collisionOn = true;
					return;
				}
				checkRightCollision(entity, etRow, ebRow, nextX / gp.tileSize);
			}
		}
	}

	public int checkObjectCollision(Entity entity, boolean player) {
		int index = 999;
		GameObject[] obs = gp.mapM.getMap().objects;
		for (int i = 0; i < obs.length; i++) {
			if (obs[i] != null) {
				entity.hitbox.x += entity.worldX;
				entity.hitbox.y += entity.worldY;
				obs[i].hitbox.x += obs[i].worldX;
				obs[i].hitbox.y += obs[i].worldY;

				switch (entity.direction) {
					case "up" -> entity.hitbox.y -= entity.speed;
					case "down" -> entity.hitbox.y += entity.speed;
					case "left" -> entity.hitbox.x -= entity.speed;
					case "right" -> entity.hitbox.x += entity.speed;
				}

				if (entity.hitbox.intersects(obs[i].hitbox)) {
					if (obs[i].collision)
						entity.collisionOn = true;
					if (player)
						index = i;
				}

				entity.hitbox.x = entity.hitboxDefaultX;
				entity.hitbox.y = entity.hitboxDefaultY;
				obs[i].hitbox.x = obs[i].hitboxDefaultX;
				obs[i].hitbox.y = obs[i].hitboxDefaultY;
			}
		}
		return index;
	}

	public void checkPlayerCollision(Entity entity) {
		entity.hitbox.x += entity.worldX;
		entity.hitbox.y += entity.worldY;
		gp.player.hitbox.x += gp.player.worldX;
		gp.player.hitbox.y += gp.player.worldY;

		switch (entity.direction) {
			case "up" -> entity.hitbox.y -= entity.speed;
			case "down" -> entity.hitbox.y += entity.speed;
			case "left" -> entity.hitbox.x -= entity.speed;
			case "right" -> entity.hitbox.x += entity.speed;
		}

		if (entity.hitbox.intersects(gp.player.hitbox))
			entity.collisionOn = true;

		entity.hitbox.x = entity.hitboxDefaultX;
		entity.hitbox.y = entity.hitboxDefaultY;
		gp.player.hitbox.x = gp.player.hitboxDefaultX;
		gp.player.hitbox.y = gp.player.hitboxDefaultY;
	}
}
