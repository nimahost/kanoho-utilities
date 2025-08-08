// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";
import mcfunction from "syntax-mcfunction/mcfunction.tmLanguage.json?raw";

// https://astro.build/config
export default defineConfig({
	output: "static",
	trailingSlash: "never",
	base: "/kanoho-utilities/",
	integrations: [
		starlight({
			title: "Kanoho Utilities",
			description: "Documentation for Kanoho custom commands and utilities.",
			customCss: ["./src/theme.css"],
			head: [
				{
					tag: "link",
					attrs: {
						rel: "icon",
						type: "image/x-icon",
						href: "/kanoho-utilities/favicon.ico",
					},
				},
			],
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
						{ label: "Getting Started", slug: "guides/getting_started" },
						{ label: "Creating a Usable Item", slug: "guides/usable_item" },
					],
				},
				{
					label: "Reference",
					items: [
						{
							label: "Commands",
							items: [
								{ label: "Velocity Command", slug: "reference/commands/velocity" },
								{ label: "Cooldown Command", slug: "reference/commands/cooldown" },
							],
						},
						{ label: "Item Components", slug: "reference/item_components" },
						{ label: "Entity Components", slug: "reference/entity_components" },
					],
				},
			],
			editLink: {
				baseUrl: "https://github.com/nimahost/kanoho-utilities/edit/main/docs/",
			},
			favicon: "./public/favicon.ico",
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
