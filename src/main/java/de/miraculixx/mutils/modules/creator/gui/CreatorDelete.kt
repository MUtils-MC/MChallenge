package de.miraculixx.mutils.modules.creator.gui

class CreatorDelete(val it: InventoryClickEvent) {

    init {
        event()
    }

    privat fun event() {
        val item = it.currentItem
        val player = it.whoClicked as Player
        val top = itemStack(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                customModel = 0
                name = "§9Delete Challenges"
                itemMeta = skullTexture(
                    this,
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0="
                )
            }
        when (val id = item?.itemMeta?.customModelData ?: 0) {
            200 -> {
                GUIBuilder(player, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
            }
            
            else -> {
                val challenge = CreatorManager.getChallenge(id) ?: return
                CreatorManager.deleteChallenge()
                player.playSound(player, SOUND.RESPAWN_ANKER_DEPLETE, 1f, 1f)
                GUIBuilder(player, GUI.CREATOR_DELETE).storage(null, )
            }
        }
    }

    private fun getAllItems(): Map<ItemStack, Boolean> {
        val tools = CreatorInvTools()
        return tools.getAllItems(cmp("Sneak click", cHighlight) + cmp(" ≫ Delete (PERMANENT)"))
    }
}