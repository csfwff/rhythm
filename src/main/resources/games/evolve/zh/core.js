//1.汉化杂项
var cnItems = {
    _OTHER_: [],
    // MQ
    'Electricity is a major advancement for your people, the future possibilities are endless.': '电力是你们人民取得的主要进步，未来有无限可能。',
    'Polymer is now available for manufacture': '现在可以制造聚合物了。',
    'Nano Tubes are now available for manufacture': '现在可以制造纳米管了。',
    'Your scientists have unlocked the secrets of the atom, the atomic age has begun.': '你的科学家揭开了原子的秘密，原子时代已经开始。',
    'Outer space treaty be damned.': '去TMD《外太空协定》',
    // 资源
    'Steel': '钢',
    'Iron': '铁',
    'Bones': '骨头',
    'Souls': '灵魂',
    'Flesh': '肉',
    'Boneweave': '骸骨织物',
    // 种族
    'Human': '人类',
    'Elf': '精灵',
    'Orc': '兽人',
    'Cath': '猫人',
    'Wolven': '狼人',
    'Centaur': '半人马',
    'Kobold': '狗头人',
    'Goblin': '哥布林',
    'Gnome': '侏儒',
    'Ogre': '食人魔',
    'Cyclops': '独眼巨人',
    'Troll': '巨魔',
    'Tortoisan': '乌龟',
    'Gecko': '壁虎',
    'Slitheryn': '娜迦',
    'Arraak': '陆行鸟',
    'Pterodacti': '翼手龙',
    'Dracnid': '天龙',
    'Ent': '树人',
    'Cacti': '仙人掌',
    'Sporgar': '孢子虫',
    'Shroomi': '蘑菇人',
    'Mantis': '螳螂',
    'Scorpid': '蝎子',
    'Antid': '蚂蚁',
    // 左侧弹出框
    'Orbit Depot': '轨道补给站',
    'Hunters': '猎人',
    'Slaves': '奴隶',
    'Craftsman': '工匠',
    'Soul Well': '灵魂井',
    'Mastery': '精通',
    'Satellite': '卫星',
    'Iridium Mine': '铱矿',
    'Helium Mine': '氦矿',
    'Power': '电力',
    'Silo': '粮仓',
    'Quarry': '采石场',
    'Reactor': '反应堆',
    'Derrick': '石油井架',
    'Coal Ash': '煤灰',
    'Obeservatory': '观测站',
    'Exotic Lab': '外太空实验室',
    'Garage': '车库',
    'Moon Base': '月球基地',
    'Outpost': '前哨站',
    'Space Station': '空间站',
    'Containment': '超铀存储',
    'Quantum': '量子',
    'Unification': '统一',
    'Elerium Ship': '超铀采矿船',
    'Iridium Ship': '铱采矿船',
    'Gas Collector': '收集器',
    'Geothermal': '地热',
    'Spaceport': '太空港',
    'Space Miners': '太空矿工',
    'Biodome': '生物穹顶',
    'Extractor': '提取器',
    // 未分类：
    'Settings': '设置',
    'Evolving': '进化中',
    'City': '城市',
    'Civics': '内政',
    'Evolve': '进化',
    'Farmer': '农民',
    'Government': '政府',
    'Jobs': '工作',
    'Light': '浅色',
    'Market': '市场',
    'Miner': '矿工',
    'New': '新',
    'Night': '夜晚',
    'Top Bar': '顶部条',
    'Stats': '统计',
    'Scientist': '科学家',
    'Crispr': '基因编辑技术',
    'Space': '太空',
    'Export Game': '导出存档',
    'Evolve by': '进化 作者:',
    'External Links': '外部链接',
    'Genetics': '遗传学',
    'Year': '年份',
    'Theme': '主题',
    'Tax Rates': '税率',
    'Taxes': '税收',
    'Shed': '窝棚',
    'Sawmill': '锯木厂',
    'Village': '村庄',
    'Import Game': '导入存档',
    'Import/Export Save': '导入/导出存档',
    'Industry': '行业',
    'Enable Reset Buttons': '启用重置按钮',
    'Entertainer': '艺人',
    'Eukaryotic Cell': '真核细胞',
    'Lumberjack': '伐木工',
    'Membrane': '细胞膜',
    'Morale': '士气',
    'Overall': '总体',
    'Race Info': '比赛信息',
    'Red Green CB': '红绿CB',
    'Dark': '暗色',
    'Sexual Reproduction': '有性生殖',
    'Nucleus': '原子核',
    'Organelles': '细胞器',
    'Plasmid': '质粒',
    'Phage': '噬菌体',
    'Prehistoric': '史前',
    'New Research': '新研究',
    'Projects': '项目',
    'Professor': '教授',
    'Professors': '教授',
    'Protoplasm': '原生质',
    'Quarry Worker': '采石工人',
    'Mitochondria': '细胞线粒体',
    'Multiplier Keys': '乘数键',
    'Space Miner': '太空矿工',
    'Research': '研究',
    'Resources': '资源',
    'Tab Navigation': '标签导航',
    'Unemployed': '失业人口',
    'Message Queue': '消息队列',
    'Day': '天数',
    'Cement Plant Worker': '水泥厂工人',
    'Phagocytosis': '吞噬',
    'Banker': '银行家',
    'Coal Miner': '煤矿工人',
    'Colonist': '殖民者',
    'Completed': '已完成',
    'Completed Research': '已完成研究',
    'Current Game': '本轮游戏',
    'Died in Combat': '在战斗中死去',
    'Creates 1 RNA': '创建1 RNA',
    'Spores': '孢子',
    'Never': '永不',
    'Club': '棍棒',
    'Bone Tools': '骨制工具',
    'Wounded': '伤兵',
    'University': '大学',
    'Rock Quarry': '采石场',
    'Mine': '矿井',
    'Foundry': '铸造厂',
    'Investing': '投资',
    'Mercenaries': '雇佣兵',
    'Plywood': '胶合板',
    'Brick': '砖',
    'Agriculture': '农业',
    'Artisans': '工艺',
    'Helium': '氦',
    'Elerium': '超铀',
    'Grain Silo': '粮仓',
    'Housing': '住房',
    'Reinforced Sheds': '加固棚',
    'Theatre': '剧院',
    'Containerization': '集装箱技术',
    'Marketplace': '市场',
    'Bronze Pickaxe': '青铜镐',
    'Bronze Hoes': '青铜锄头',
    'Irrigation': '灌溉',
    'Banking': '银行',
    'Currency': '货币',
    'Leather Armor': '皮甲',
    'Cement': '水泥',
    'Bows': '弓',
    'Garrison': '驻军',
    'Metal Working': '金属加工',
    'Mining': '采矿',
    'Basic Storage': '基本存储',
    'Sundial': '日晷',
    'Farm Houses': '农舍',
    'Bronze Sledgehammer': '青铜锤',
    'Trade Routes': '贸易路线',
    'Cement Factory': '水泥厂',
    'Wrought Iron': '锻铁',
    'Sheet Metal': '金属板',
    'Master Craftsman': '工匠大师',
    'Carpentry': '木匠',
    'Apprentices': '学徒',
    'Playwright': '剧作家',
    'Techno Wizards': '高科技法师',
    'Bank Vault': '银行金库',
    'Research Grants': '研究基金',
    'House Safe': '房屋保险箱',
    'Scientific Journal': '科学期刊',
    'Adjunct Professors': '兼职教授',
    'Diplomacy': '外交',
    'Dynamite': '炸药',
    'Electric Arc Furnace': '电弧炉',
    'Electric Jackhammer': '电动手提钻',
    'Electricity': '电力',
    'Electronics': '电子学',
    'Factory': '工厂',
    'Fuel Depot': '燃料库',
    'Gantry Cranes': '门式起重机',
    'Titanium Axe': '钛金斧',
    'Thesis Papers': '学术论文',
    'Titanium Banded Crates': '钛条箱',
    'Titanium Drills': '钛金钻',
    'Titanium Hoes': '钛金锄',
    'Titanium Sledgehammer': '钛金锤',
    'Iron Axe': '铁斧',
    'Iron Hoes': '铁锄',
    'Iron Pickaxe': '铁镐',
    'Iron Sledgehammer': '铁锤',
    'Jackhammer': '手提钻',
    'Kroll Process': '克罗尔工艺',
    'Large Volume Trading': '大宗交易',
    'Library': '图书馆',
    'Machine Gun': '机关枪',
    'Machinery': '机械',
    'Signing Bonus': '签约金',
    'Sawmills': '锯木厂',
    'Vocational Training': '职业培训',
    'Brickworks': '砖瓦厂',
    'Oxygen Converter': '氧气转换器',
    'Bessemer Process': '转炉炼钢法',
    'Blast Furnace': '高炉',
    'Crucible Steel': '坩埚钢',
    'Smelting': '冶炼',
    'Bayer Process': '拜耳法',
    'Rotary Kiln': '回转炉',
    'Coal Mining': '煤炭开采',
    'Windmill': '风车',
    'Grain Mill': '谷物磨粉机',
    'Aphrodisiac': '催欲剂',
    'Cottages': '茅屋',
    'Apartments': '公寓',
    'Radio': '广播',
    'Barns': '谷仓',
    'Warehouse': '仓库',
    'Reinforced Crates': '加强板条箱',
    'Cranes': '起重机',
    'Alloy Containers': '合金集装箱',
    'Corrupt Politicians': '腐败的政客',
    'Freight Trains': '货运列车',
    'Wharfs': '码头',
    'Savings Bonds': '储蓄债券',
    'Tesla Coil': '特斯拉线圈',
    'Internet': '互联网',
    'Industrialization': '工业化',
    'Mine Conveyor Belts': '矿山输送带',
    'Oil Derrick': '石油井架',
    'Flintlock Rifle': '燧发步枪',
    'Plate Armor': '板甲',
    'Black Powder': '黑火药',
    'ANFO': '铵油炸药',
    'Rebar': '螺纹钢',
    'Hunter Process': '亨特工艺',
    'Portland Cement': '硅酸盐水泥',
    'Oil Powerplant': '石油发电厂',
    'Series EE Bonds': 'EE系列债券',
    'Wind Turbine': '风力涡轮机',
    'Security Cameras': '安全摄像头',
    'Safety Deposit Box': '保险箱',
    'Bioscience': '生物科学',
    'Uranium Extraction': '铀萃取',
    'Alloy Drills': '合金钻头',
    'Bunk Beds': '双层床',
    'Screw Conveyor': '螺旋输送机',
    'Assembly Line': '流水线',
    'Television': '电视',
    'Casino': '赌场',
    'Base': '基础',
    'Apartment': '公寓',
    'Amphitheatre': '露天剧场',
    'Farm': '农场',
    'Cottage': '茅屋',
    'Metal Refinery': '金属精炼厂',
    'Intelligence': '智力',
    'Aluminium': '铝',
    'Coal Mine': '煤矿',
    'Coal Powerplant': '煤电厂',
    'Containers': '集装箱',
    'Bank': '银行',
    'Bonds': '债券',
    'Rating': '评分',
    'Miners': '矿工',
    'Lumber': '木材',
    'Lumber Yard': '伐木场',
    'Lumberjacks': '伐木工',
    'Powerplant': '发电厂',
    'Wardenclyffe': '沃登克里弗塔',
    'Weather': '天气',
    'Wharf': '码头',
    'Workers': '工人',
    'Polymer': '聚合物',
    'Smelter': '冶炼厂',
    'Trade Post': '贸易站',
    'Trade': '贸易',
    'Mills': '磨坊',
    'Refinery': '精炼厂',
    'C.Plant': '水泥厂',
    'GM Food': '转基因食品',
    'No bonfires please': '请不要生火',
    'Requires Coal': '需要煤',
    'Refines aluminium': '精炼铝',
    'Requires Oil': '需要石油',
    'Roxxon': '信号开关',
    'Soldiers': '士兵',
    'Employs miners': '雇佣矿工',
    'To Empty': '耗尽',
    'To Full': '充满',
    'Crates': '板条箱',
    'Flintlock Rifles': '燧发步枪',
    'Breeder Reactor': '增殖反应堆',
    'Ancalagon': '安卡拉贡',
    'Advanced Robotics': '先进机器人',
    'Buy the World': '买全世界',
    'Conquest': '征服',
    'Mythril': '秘银',
    'Level -': '等级 -',
    'Mutate': '变异',
    'Tourist Center': '旅游中心',
    'Temple': '寺庙',
    'GPS Satellite': 'GPS卫星',
    'GPS Constellation': 'GPS星座',
    'Slayer': '杀手',
    'Sun': '太阳',
    'Onyx': '缟玛瑙',
    'Onyx Control Tower': '缟玛瑙控制塔',
    'Mythril Beams': '秘银横梁',
    'Mythril Containers': '秘银集装箱',
    'Mythril Drills': '秘银钻',
    'Mythril Plated Crates': '秘银板箱',
    'Mythril Vault': '秘银保险库',
    'Ancalagon Fuel Depot': '安卡拉贡燃料库',
    'Draco': '天龙星座',
    'Atmospheric Mining': '大气开采',
    'Navigation Beacon': '导航灯塔',
    'Fracking': '水力压裂',
    'Uranium Ash': '铀灰',
    'Uranium Storage': '铀存储',
    'Nuclear Fission': '核裂变',
    'Rocketry': '火箭技术',
    'Hedge Funds': '对冲基金',
    'Stock Exchange': '证券交易所',
    'Fire Proof Safe': '防火防弹柜',
    'Monuments': '纪念碑',
    'Tourism': '旅游',
    'Genome': '基因组',
    'Genes': '基因',
    'Rail Guns': '轨道炮',
    'Space Marines': '太空陆战队',
    'Kevlar': '凯夫拉纤维',
    'Pynn Particals': '派恩粒子',
    'Matter Compression': '物质压缩',
    'Higgs Boson': '希格斯玻色子',
    'Indoctrination': '教化',
    'Space Manufacturing': '太空制造业',
    'Colonization': '殖民',
    'Star Charts': '星图',
    'Bio Lab': '生物实验室',
    'Farmers': '农民',
    'Farms': '农场',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '': '',
    '\n                                        Achievements\n                                   ': '成就',
    '\n                                        Market\n                                   ': '市场',
    '\n                                        Mass Ejector\n                                   ': '质量喷射器',
    '\n                                        Stats\n                                   ': '统计',
    '\n                                        Storage\n                                   ': '存储',
    '\n                                Civics\n                           ': '文明',
    '\n                                Civilization\n                           ': '内政',
    '\n                                Evolve\n                           ': '进化',
    '\n                                Research\n                           ': '研究',
    '\n                                Resources\n                           ': '资源',
    '\n                                Settings\n                           ': '设置',
    '\n                                Stats\n                           ': '统计',
    //原样
    'DNA': 'DNA',
    'kW': 'kW',
    //    'RNA': '核糖核酸',
}

//需处理的前缀
var cnPrefix = {
    "(-": "(-",
    "(+": "(+",
    "(": "(",
    "-": "-",
    "+": "+",
    " ": " ",
    ": ": "： ",
}

//需处理的后缀
var cnPostfix = {
    ":": "：",
    "：": "：",
    ": ": "： ",
    "： ": "： ",
    " ": "",
    "/s)": "/s)",
    "/s": "/s",
    ")": ")",
    "%": "%",
}

//需排除的，正则匹配
var cnExcludeWhole = [
    /^x?\d+(\.\d+)?[A-Za-z%]{0,2}(\s.C)?\s*$/, //12.34K,23.4 °C
    /^x?\d+(\.\d+)?(e[+\-]?\d+)?\s*$/, //12.34e+4
    /^\s*$/, //纯空格
    /^\d+(\.\d+)?[A-Za-z]{0,2}.?\(?([+\-]?(\d+(\.\d+)?[A-Za-z]{0,2})?)?$/, //12.34M (+34.34K
    /^(\d+(\.\d+)?[A-Za-z]{0,2}\/s)?.?\(?([+\-]?\d+(\.\d+)?[A-Za-z]{0,2})?\/s\stot$/, //2.74M/s (112.4K/s tot
    /^\d+(\.\d+)?(e[+\-]?\d+)?.?\(?([+\-]?(\d+(\.\d+)?(e[+\-]?\d+)?)?)?$/, //2.177e+6 (+4.01+4
    /^(\d+(\.\d+)?(e[+\-]?\d+)?\/s)?.?\(?([+\-]?(\d+(\.\d+)?(e[+\-]?\d+)?)?)?\/s\stot$/, //2.177e+6/s (+4.01+4/s tot
];
var cnExcludePostfix = [
    /:?\s*x?\d+(\.\d+)?(e[+\-]?\d+)?\s*$/, //12.34e+4
    /:?\s*x?\d+(\.\d+)?[A-Za-z]{0,2}$/, //: 12.34K, x1.5
]

//正则替换，带数字的固定格式句子
var cnRegReplace = new Map([
    [/^Some DNA molecules have replicated, you gain (\d+) DNA.$/, '一些DNA分子已复制，你获得 $1 个DNA。'],
    [/^(\d+) \/$/, '$1 \/'],
    [/^Achievements Earned: (\d+) of$/, '成就获得：$1 \/'],
    [/^Automatically consume (\d+) RNA to create (\d+) DNA$/, '自动消耗$1个RNA以产生$2个DNA'],
    [/^Automatically generate (\d+) RNA$/, '自动生成$1个RNA'],
    [/^Turn (\d+) RNA into (\d+) DNA$/, '将$1个RNA转化为$2个DNA'],
    [/^(\d+) Trade Routes$/, '$1 贸易路线'],
    [/^\+(\d+)% Crafted Materials$/, '\+$1％ 精制材料'],
    [/^(\d+)% Knowledge Production$/, '$1％ 知识产量'],
    [/^(\d+) Max Containers$/, '$1 集装箱上限'],
    [/^(\d+) Max Crates$/, '$1 箱子上限'],
    [/^([\d\.]+) Iridium Production$/, '$1 铱生产'],
    [/^([\d\.]+) Max Knowledge$/, '$1 知识上限'],
]);

//2.采集新词
//20190320@JAR


var cnItem = function () {

    //传参是否非空字串
    if (!arguments[0]) return "";

    //检验传参是否对象
    let text = arguments[0],
        s = '';
    if (typeof (text) != "string")
        return text;
    else
        s = arguments[0].charCodeAt();

    //检验传参是否英文
    // if (
    //     s < 65 || (s > 90 && s < 97) || (s > 122)
    //
    // ) return text;

    //处理前缀
    let text_prefix = "";
    for (let prefix in cnPrefix) {
        if (text.substr(0, prefix.length) === prefix) {
            text_prefix = cnPrefix[prefix];
            text = text.substr(prefix.length);
        }
    }
    //处理后缀
    let text_postfix = "";
    for (let postfix in cnPostfix) {
        if (text.substr(-postfix.length) === postfix) {
            text_postfix = cnPostfix[postfix];
            text = text.substr(0, text.length - postfix.length);
        }
    }
    //处理正则后缀
    let text_reg_exclude_postfix = "";
    for (let reg of cnExcludePostfix) {
        let result = text.match(reg);
        if (result) {
            text_reg_exclude_postfix = result[0];
            text = text.substr(0, text.length - text_reg_exclude_postfix.length);
        }
    }

    //检验字典是否可存
    if (!cnItems._OTHER_) cnItems._OTHER_ = [];

    //检查是否排除
    for (let reg of cnExcludeWhole) {
        if (reg.test(text)) {
            return arguments[0];
        }
    }

    //尝试正则替换
    for (let [key, value] of cnRegReplace.entries()) {
        if (key.test(text)) {
            return text_prefix + text.replace(key, value) + text_reg_exclude_postfix + text_postfix;
        }
    }

    //遍历尝试匹配
    for (let i in cnItems) {
        //字典已有词汇或译文、且译文不为空，则返回译文
        if (
            text == i || text == cnItems[i] &&
            cnItems[i] != ''
        )
            return text_prefix + cnItems[i] + text_reg_exclude_postfix + text_postfix;
    }

    //调整收录的词条，0=收录原文，1=收录去除前后缀的文本
    let save_cfg = 1;
    let save_text = save_cfg ? text : arguments[0]
    //遍历生词表是否收录
    for (
        let i = 0; i < cnItems._OTHER_.length; i++
    ) {
        //已收录则直接返回
        if (save_text == cnItems._OTHER_[i])
            return arguments[0];
    }

    if (cnItems._OTHER_.length < 500) {
        //未收录则保存
        cnItems._OTHER_.push(save_text);
        cnItems._OTHER_.sort(
            function (a, b) {
                return a.localeCompare(b)
            }
        );
    }

    /*
        //开启生词打印
        //console.log(
            '有需要汉化的英文：', text
        );
    */

    //返回生词字串
    return arguments[0];
};

transTaskMgr = {
    tasks: [],
    addTask: function (node, attr, text) {
        this.tasks.push({
            node,
            attr,
            text
        })
    },
    doTask: function () {
        let task = null;
        while (task = this.tasks.pop())
            task.node[task.attr] = task.text;
    },
}

function TransSubTextNode(node) {
    if (node.childNodes.length > 0) {
        for (let subnode of node.childNodes) {
            if (subnode.nodeName === "#text") {
                let text = subnode.textContent;
                let cnText = cnItem(text);
                cnText !== text && transTaskMgr.addTask(subnode, 'textContent', cnText);
                //console.log(subnode);
            } else if (subnode.nodeName !== "SCRIPT" && subnode.nodeName !== "TEXTAREA") {
                if (!subnode.childNodes || subnode.childNodes.length == 0) {
                    let text = subnode.innerText;
                    let cnText = cnItem(text);
                    cnText !== text && transTaskMgr.addTask(subnode, 'innerText', cnText);
                    //console.log(subnode);
                } else {
                    TransSubTextNode(subnode);
                }
            } else {
                // do nothing;
            }
        }
    }
}

! function () {
    console.log("加载汉化模块");

    let observer_config = {
        attributes: false,
        characterData: true,
        childList: true,
        subtree: true
    };
    let targetNode = document.body;
    //汉化静态页面内容
    TransSubTextNode(targetNode);
    transTaskMgr.doTask();
    //监听页面变化并汉化动态内容
    let observer = new MutationObserver(function (e) {
        //window.beforeTransTime = performance.now();
        observer.disconnect();
        for (let mutation of e) {
            if (mutation.target.nodeName === "SCRIPT" || mutation.target.nodeName === "TEXTAREA") continue;
            if (!mutation.target.childNodes || mutation.target.childNodes.length == 0) {
                mutation.target.innerText = cnItem(mutation.target.innerText);
            } else if (mutation.target.nodeName === "#text") {
                mutation.target.textContent = cnItem(mutation.target.textContent);
            } else if (mutation.addedNodes.length > 0) {
                for (let node of mutation.addedNodes) {
                    if (node.nodeName === "#text") {
                        node.textContent = cnItem(node.textContent);
                        //console.log(node);
                    } else if (node.nodeName !== "SCRIPT" && node.nodeName !== "TEXTAREA") {
                        if (!node.childNodes || node.childNodes.length == 0) {
                            node.innerText = cnItem(node.innerText);
                        } else {
                            TransSubTextNode(node);
                            transTaskMgr.doTask();
                        }
                    }
                }
            }
        }
        observer.observe(targetNode, observer_config);
        //window.afterTransTime = performance.now();
        //console.log("捕获到页面变化并执行汉化，耗时" + (afterTransTime - beforeTransTime) + "毫秒");
    });
    observer.observe(targetNode, observer_config);
}();