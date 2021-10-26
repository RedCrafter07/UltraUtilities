package com.redcrafter07.processed.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.function.Consumer;

public class ProcessorSwordItem extends SwordItem {

    public ProcessorSwordItem(IItemTier itemTier, int attackDamage, float attackSpeedIn, Properties builderIn) {
        super(itemTier, attackDamage, attackSpeedIn, builderIn);
    }

    /*@Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(stack.getDamage() == 1)  {
            return false;
        }
        stack.damageItem(1, attacker, (entity) -> {
        });
      return true;
    }*/

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        /*if(stack.getDamage() == 1)  {
            return 0;
        }*/
        return super.damageItem(stack, amount, entity, onBroken);
    }


    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> {
            entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
    }

    /*public ActionResult<?> setAttackDamage(int attackDamage, PlayerEntity player)    {
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        return  ActionResult.resultSuccess(stack);
    }*/


}