/*
 * Copyright (c) 2020 Sybsuper
 * All Rights Reserved
 *
 * Do not use this code without permission of the developer.
 */

package me.sybsuper.SybSuperLore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Main extends JavaPlugin {
	public FileConfiguration config;

	@Override
	public void onEnable() {
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		Iterator<Recipe> iter = getServer().recipeIterator();
		List<Recipe> recipeList = new ArrayList<>();
		while (iter.hasNext()) {
			Recipe r = iter.next();
			if (r instanceof ShapelessRecipe) {
				ShapelessRecipe recipe = (ShapelessRecipe) r;
				List<ItemStack> ingredients = recipe.getIngredientList();
				ItemStack result = recipe.getResult();
				ItemMeta meta = result.getItemMeta();
				List<String> lores = config.getStringList("lores");
				for (int i = 0; i < lores.size(); i++) {
					lores.set(i, ChatColor.translateAlternateColorCodes('&', lores.get(i)));
				}
				meta.setLore(lores);
				result.setItemMeta(meta);
				ShapelessRecipe newRecipe = new ShapelessRecipe(result);
				for (ItemStack ingredient : ingredients) {
					newRecipe.addIngredient(ingredient.getAmount(), ingredient.getType());
				}
				recipeList.add(newRecipe);
				iter.remove();
			} else if (r instanceof ShapedRecipe) {
				ShapedRecipe recipe = (ShapedRecipe) r;
				Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
				ItemStack result = recipe.getResult();
				ItemMeta meta = result.getItemMeta();
				List<String> lores = config.getStringList("lores");
				for (int i = 0; i < lores.size(); i++) {
					lores.set(i, ChatColor.translateAlternateColorCodes('&', lores.get(i)));
				}
				meta.setLore(lores);
				result.setItemMeta(meta);
				String[] shape = recipe.getShape();
				ShapedRecipe newRecipe = new ShapedRecipe(result);
				newRecipe.shape(shape);
				for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
					Character c = entry.getKey();
					ItemStack i = entry.getValue();
					if (i != null) {
						try {
							newRecipe.setIngredient(
									c,
									i.getData().getItemType()
							);
							recipeList.add(newRecipe);
							iter.remove();
						} catch (NullPointerException e) {
							getLogger().log(Level.WARNING, "Result: " + result.getType().toString());
						}
					}
				}
			}
		}
		for (Recipe r : recipeList) {
			Bukkit.addRecipe(r);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("sybsuperlore.reload")) {
			reloadConfig();
			config = getConfig();
			config.options().copyDefaults(true);
			saveConfig();
			sender.sendMessage("Reloaded config.yml file.");
		}
		return true;
	}
}
