# Transform player into Apostle form
effect give @s minecraft:strength 300 1
effect give @s minecraft:speed 300 1
effect give @s minecraft:night_vision 300 0
effect give @s minecraft:fire_resistance 300 0

# Visual effects
particle minecraft:soul_fire_flame ~ ~1 ~ 1 1 1 0.1 50
playsound minecraft:entity.wither.spawn player @s ~ ~ ~ 1.0 0.5

# Send message
tellraw @s {"text":"You have transformed into an Apostle!","color":"dark_red","bold":true}