// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";
import mcfunction from "syntax-mcfunction/mcfunction.tmLanguage.json?raw";

// https://astro.build/config
export default defineConfig({
	integrations: [
		starlight({
			title: "Kanoho Utilities",
			description: "Documentation for Kanoho custom commands and utilities.",
			customCss: ["./src/theme.css"],
			social: [
				{
					icon: "github",
					label: "GitHub",
					href: "https://github.com/nimahost/kanoho-utilities",
				},
			],
			sidebar: [
				{
					label: "Guides",
					items: [
						// Each item here is one entry in the navigation menu.
						{ label: "Example Guide", slug: "guides/example" },
					],
				},
				{
					label: "Reference",
					items: [],
				},
			],
			editLink: {
				baseUrl: "https://github.com/nimahost/kanoho-utilities/edit/main/docs/",
			},
			logo: {
				src: "./public/favicon.svg",
			},
			expressiveCode: {
				styleOverrides: { borderRadius: "0.25rem" },
				shiki: {
					langs: [JSON.parse(mcfunction)],
					langAlias: { mcfunction: "Syntax Mcfunction" },
				},
			},
		}),
	],
});
