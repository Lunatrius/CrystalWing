package bspkrs.crystalwing;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import bspkrs.util.CommonUtils;

public class ItemCrystalWingBurned extends Item
{

    private Random rand;
    private int    teleDistance;

    public ItemCrystalWingBurned(int j)
    {
        teleDistance = j;
        rand = new Random();

        if (!CommonUtils.isObfuscatedEnv())
            this.setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public Item setUnlocalizedName(String par1Str)
    {
        super.setUnlocalizedName(par1Str);
        this.setTextureName(par1Str.replaceAll("\\.", ":"));
        return this;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityPlayer)
    {
        if (!world.isRemote)
        {
            if (entityPlayer.dimension == -1)
            {
                return CWSettings.crystalWing.onItemRightClick(itemstack, world, entityPlayer);
            }
            world.playSoundAtEntity(entityPlayer, "fire.ignite", 1.0F, 0.5F);
            int dX = (int) ((entityPlayer.posX + rand.nextInt(teleDistance * 2)) - teleDistance);
            int dZ = (int) ((entityPlayer.posZ + rand.nextInt(teleDistance * 2)) - teleDistance);
            int i = CommonUtils.getFirstNonAirBlockFromTop(world, dX, dZ);

            world.getChunkProvider().loadChunk(dX - 3 >> 4, dZ - 3 >> 4);
            world.getChunkProvider().loadChunk(dX + 3 >> 4, dZ - 3 >> 4);
            world.getChunkProvider().loadChunk(dX - 3 >> 4, dZ + 3 >> 4);
            world.getChunkProvider().loadChunk(dX + 3 >> 4, dZ + 3 >> 4);
            entityPlayer.rotationPitch = 0.0F;
            entityPlayer.rotationYaw = 0.0F;
            entityPlayer.setPositionAndUpdate(dX + 0.5D, i + 0.1D, dZ);

            while (!world.getCollidingBoundingBoxes(entityPlayer, entityPlayer.boundingBox).isEmpty())
            {
                entityPlayer.setPositionAndUpdate(entityPlayer.posX, entityPlayer.posY + 1.0D, entityPlayer.posZ);
            }

            world.playSoundAtEntity(entityPlayer, "mob.endermen.portal", 1.0F, 1.0F);
            CommonUtils.spawnExplosionParticleAtEntity(entityPlayer);

            itemstack.stackSize--;
        }
        return itemstack;
    }
}
