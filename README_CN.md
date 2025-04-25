<div align="center"><img src="asset/minechat-logo.png"/></div>
<p align="center"><strong><span style="font-size: 1.25em;">将GPT接入Minecraft的Forge模组</span></strong></p>
<p align="center">
  <a href="README.md"><img src="https://img.shields.io/badge/Language-English-blue.svg"></a>
  <a href="README_CN.md"><img src="https://img.shields.io/badge/Language-简体中文-red.svg"></a>
</p>
<div align="center"><img src="asset/minechat-cover-comp.png"/></div>

------------------------------
# Mine Chat

现在你仅需要一点简单的配置即可让村民“活”起来。面对村民发送文本消息，村民将与你聊天。

### ✨ 模组特点
- 实体拥有独立记忆，支持连续多轮对话
- 同时支持 **客户端** 与 **服务端**
- 提供丰富的可配置选项
- 将聊天行为与原版游戏机制融合
- 更多待挖掘的功能……

### 🏷️ 版本兼容性

- **Minecraft**: `1.20.4`
- **Forge**: `49.1.0` ~ `49.2.0`


### ⚙️ 基础配置

编辑位于 `config/minechat-common.toml` 的配置文件，修改以下几个必须的配置项:

| Name        | Description |
|-------------|-------------|
| `gpt_api`   | 云模型的api地址   |
| `gpt_key`   | 云模型调用的key   |
| `gpt_model` | 使用的云模型名称    |

此外，您可以为不同职业的村民定义prompt，或配置他们共享的默认prompt。有关参数请参阅配置文件中的注释。

> 💡 **Tip**: 在多人模式下，仅编辑服务器中的配置文件即可

### 🔐 权限管理

您可以通过编辑配置文件来添加黑名单、白名单条目，以及是否启用白名单和启动mod。

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

> 📝 当 `use_white_list` 设置为true时，仅 `white_list` 中的玩家可使用该模组

###  🧰 附加功能

#### 💵 交易调整

这是一个可选功能，当你启用时，你的行为将影响村民对你的态度，这会使他们重新调整商品的定价策略。

相关配置如下：

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

> 📝 > 注意 `discount_turns` 的配置策略：
>
> 设置过小时，可能会加速大模型token的消耗；设置过大时，可能影响对话速度
