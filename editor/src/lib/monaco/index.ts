import type { Attachment } from "svelte/attachments";
import type * as monaco from "monaco-editor";

type Monaco = typeof monaco;

export const editor: Attachment<HTMLElement> = (element) => {
	let editor: monaco.editor.IStandaloneCodeEditor | undefined;
	let monaco: Monaco | undefined;

	import("./client").then(async ({ monaco }) => {
		editor = monaco.editor.create(element, {
			theme: "catppuccin-macchiato",
			language: "mcfunction",
			automaticLayout: true,
		});
	});

	return () => {
		editor?.dispose();
		monaco?.editor.getModels().forEach((model) => model.dispose());
	};
};
