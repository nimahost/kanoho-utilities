<script lang="ts">
	import type { Tree, TreeItem } from "melt/builders";
	import { slide } from "svelte/transition";
	import NodeList from "./NodeList.svelte";

	let { children, level = 0 }: { children: Tree<TreeItem>["children"]; level?: number } = $props();
</script>

{#each children as node}
	<li style="list-style-type: none">
		<button {...node.attrs}>
			{#each { length: level }}<span class="indent"></span>{/each}
			<span class="label">
				{node.id.split("/").pop()}
			</span>
		</button>
		{#if node.children && node.expanded}
			<ol transition:slide={{ duration: 150 }}>
				<NodeList children={node.children} level={level + 1} />
			</ol>
		{/if}
	</li>
{/each}

<style lang="scss">
	button {
		transition: background-color 150ms ease-in-out;
		background-color: transparent;
		padding: 0 0 0 10px;
		text-align: left;
		font-size: 14px;
		color: inherit;
		border: none;
		width: 100%;

		&:hover,
		&:focus {
			background-color: var(--vscode-list-hoverBackground);
		}

		&[data-selected] {
			background-color: var(--vscode-list-focusBackground);
		}

		.label {
			padding: 1px;
		}

		.indent {
			border-left: 1px solid var(--vscode-tree-indentGuidesStroke);
			margin: 0 10px 0 9px;
		}
	}
</style>
