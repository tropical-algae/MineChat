<div align="center"><img src="asset/minechat-logo.png"/></div>
<p align="center"><strong><span style="font-size: 1.25em;">A Forge-based mod that integrates GPT into Minecraft</span></strong></p>
<p align="center">
  <a href="README.md"><img src="https://img.shields.io/badge/Language-English-blue.svg"></a>
  <a href="README_CN.md"><img src="https://img.shields.io/badge/Language-简体中文-red.svg"></a>
</p>
<div align="center"><img src="asset/minechat-cover-comp.png"/></div>

------------------------------
# Mine Chat

Now you could chat with villager in minecraft with a simple config, just face to them and texting.

### ✨ Feature

- Each entity has an independent memory, supporting coherent multi-turn conversations
- Supports both **client** and **server** sides.
- Offers a wide range of configurable options
- Integrates chat behavior with vanilla game mechanics
- More features awaiting discovery…

### 🏷️ Version Compatibility

- **Minecraft**: `1.20.4`
- **Forge**: `49.1.0` ~ `49.2.0`

### ⚙️ Basic Configuration

Edit the configuration file located at `config/minechat-common.toml`. The following parameters are required:

| Name        | Description                                     |
|-------------|-------------------------------------------------|
| `gpt_api`   | Model URL compatible with the OpenAI API format |
| `gpt_key`   | API key for accessing the model                 |
| `gpt_model` | Name of the model to be used                    |

Additionally, you can define custom prompts for villagers of different professions, or specify a default prompt to be shared among them. Please refer to the comments in the configuration file for available parameters.

> 💡 **Tip**: In multiplayer mode, configuring the server alone is sufficient.

### 🔐 Authorization Manage

You can configure the mod by editing the configuration file, including adding blacklist/whitelist entries, enabling the whitelist, and activating the mod.

```toml
#Config for player authorization 
[authorization]
    #Allow entities to speak or not. If false, entities will not chat anymore.
    mod_enable = true
    #Use white list or not. If True, only the players who on the white_list can chat with entities (villager).
    use_white_list = false
    #Player white list
    white_list = ["Steve", "Alex"]
    #Player black list. Specify the list of player names to block from interacting with entities (villagers).
    black_list = []
```

> 📝 When the `use_white_list` is true, only players listed in `white_list` are allowed to use the AI entity chat feature.

###  🧰 Additional Features

#### 💵 Trade Adjustment

This is an optional feature. When enabled, your behavior will influence villagers’ attitudes toward you, which in turn affects how they adjust their pricing strategies.

The related configuration is as follows:

```toml
#Config for villager trade
[trade_adjustment]
    #Enable trade adjustment. If True, the transaction with villagers will be influenced by your actions.
    trade_adjust_enabled = true
    #The required number of conversations triggered by discount detection
    discount_turns = 12
    #Maximum price fluctuation ratio for transaction
    #Range: 0.0 ~ 1.0
    max_cost_adjust_ratio = 0.5
```

> 📝 > Take care when configuring `discount_turns` :
> 
> if too **low**, it may accelerate token consumption for the LLM; if too **high**, it might slow down the pacing of conversations.