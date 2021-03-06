/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.component.UserDataComponent;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxglgames.spacerunner.control.EnemyControl;
import com.almasb.fxglgames.spacerunner.control.KeepOnScreenControl;
import com.almasb.fxglgames.spacerunner.control.PlayerControl;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
@Singleton
public class SpaceRunnerFactory implements EntityFactory {

    @Spawns("Player")
    public GameEntity newPlayer(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.PLAYER)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_player.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new KeepOnScreenControl(false, true))
                .build();
    }

    public GameEntity newBullet(double x, double y, SpaceRunnerType ownerType) {
        return Entities.builder()
                .type(SpaceRunnerType.BULLET)
                .at(x, y - 5.5)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true), new UserDataComponent(ownerType))
                .with(new ProjectileControl(new Point2D(ownerType == SpaceRunnerType.PLAYER ? 1 : -1, 0), 250),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Enemy1")
    public GameEntity newEnemy(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.ENEMY)
                .at(data.getX(), data.getY())
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("spacerunner/sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }
}
