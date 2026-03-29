package ec.brooke.kanoho.features.props;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;

public class WrenchSuppressor extends Interaction {
    private final Player player;

    public WrenchSuppressor(Player player) {
        super(EntityType.INTERACTION, player.level());
        this.player = player;

        this.setHeight(2);
        this.setWidth(2);
    }

    @Override
    public void tick() {
        this.setPos(player.getEyePosition().subtract(0, 1, 0));
        super.tick();
    }
}
