package pl.elytraboost.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientInteractionManagerMixin {

    /**
     * Backport z 1.21.6 (tylko klient): gdy lecisz na elytrze z fajerwerkiem w ręce,
     * klient nie wysyła pakietu "użyj na bloku", tylko zwraca PASS. Dzięki temu
     * MinecraftClient#doItemUse przechodzi do zwykłego użycia przedmiotu,
     * które wysyła pakiet "use item" i serwer przyspiesza gracza.
     */
    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void elytraboost$skipBlockUseWhenGliding(ClientPlayerEntity player, Hand hand,
                                                     BlockHitResult hitResult,
                                                     CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isGliding() && stack.getItem() instanceof FireworkRocketItem) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
