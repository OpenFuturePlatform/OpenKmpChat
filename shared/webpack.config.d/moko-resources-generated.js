const path = require('path');

const mokoResourcePath = path.resolve("D:\OPEN\KotlinMultiPlatform\HarvestTimeKMP\shared\build\generated\moko\jsMain\commutualmobileharvestKmp\res");

config.module.rules.push(
    {
        test: /\.(.*)/,
        include: [
            path.resolve(mokoResourcePath)
        ],
        type: 'asset/resource'
    }
);

config.module.rules.push(
    {
        test: /\.(otf|ttf)?$/,
        use: [
            {
                loader: 'file-loader',
                options: {
                    name: '[name].[ext]',
                    outputPath: 'fonts/'
                }
            }
        ]
    }
)

config.resolve.modules.push(
    path.resolve(mokoResourcePath)
);