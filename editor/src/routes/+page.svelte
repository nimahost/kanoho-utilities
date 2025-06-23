<script lang="ts">
	import { editor } from "$lib/monaco";
	import "greset";
</script>

<div>
	<div class="container" {@attach editor}></div>
</div>

<style>
	.container {
		width: 100%;
		height: 600px;
	}
</style>
