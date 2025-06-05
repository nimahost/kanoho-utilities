---
title: Example Guide
description: A guide in my new Starlight docs site.
---

```mcfunction
execute at @a[
    nbt= {
        FallFlying:1b,
        kanoho: {
            consumed: {
                components: {
                    "minecraft:custom_data": {
                        item: "doc_alcubierre"
                    }
                }
            }
        }
    }
] run particle minecraft:flash ~ ~ ~ 2 2 2 0 10 force @a
```
