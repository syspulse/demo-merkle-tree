<script>
	import Icon, { directions, iconNames } from "./Icon.svelte";
	// import Moveable from "svelte-moveable";
	import { createEventDispatcher } from 'svelte';

	const dispatch = createEventDispatcher();

	export let level = 0;
	export let name = "";
	export let xpos = 0;
	export let ypos = 0;
	export let w = 188;
	export let h = 128;
	export let data = 0;
	export let icon = "arrow";
	export let x_parent = -1;
	export let y_parent = -1;
	
	let blinker = 0;

	$: {
		// just reference telemetry to trigger the change of blinked
		blinker = blinker + 1;
		//console.log("Blink:", name, blinker, xpos,ypos, w, h);
	}

	console.log("INIT", name, blinker, xpos, ypos, w, h);
</script>

<style>
	.node {
		fill: rgb(235, 237, 240);
	}

	.node-empty {
		fill: rgb(251, 253, 253);
	}

	.data-0 {
		fill: rgb(235, 237, 240);
	}
	.data-1-0 {
		fill: rgb(64, 196, 99);
		animation: data-1-keyframes-1 0.5s 1;
	}
	.data-1-1 {
		fill: rgb(64, 196, 99);
		animation: data-1-keyframes-2 0.5s 1;
	}

	@keyframes data-1-keyframes-1 {
		from {
			fill: rgb(235, 237, 240);
		}
		to {
			fill: rgb(64, 196, 99);
		}
	}
	@keyframes data-1-keyframes-2 {
		from {
			fill: rgb(235, 237, 240);
		}
		to {
			fill: rgb(64, 196, 99);
		}
	}

	.data-hash,
	.data-leaf,
	.data-root,
	.data-empty,
	.debug,
	.text,
	.name,
	.telemetry-name,
	.telemetry-value {
		fill: rgb(106, 101, 116);
		display: block;
		font-size: 13px;
		line-height: 15px;
		font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Helvetica,
			Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji;
		text-align: left;
		white-space: nowrap;
	}

	.data-root {
		font-weight: bold;
		fill: darkgreen;
	}

	.data-hash {
		fill: black;
	}

	.data-leaf {
		font-weight: bold;
		fill: green;
	}
	.data-empty {
		fill: gray;
	}

	.debug {
		font-size: 9px;
	}

	.telemetry-name {
		fill: rgb(106, 101, 116);
	}
	.telemetry-value {
		fill: black;
	}

	.border {
		/* fill:rgb(${color}); */
		stroke-width: 1;
		stroke: rgb(88, 96, 105);
	}

	.border-moving {
		stroke-width: 2;
		stroke: lightblue;
	}

	.icon {
		font-size: 2em;
	}

	.draggable {
  		cursor: move;
	}
</style>

<svg class="target" x={xpos} y={ypos}

>
	<g transform="translate(0,0)">
		<rect x="0" y="0" width={w} height={h} class={"border "+"node"+ (data==0 ? "-empty" : "")}>
			<title>{name}</title>
		</rect>

		<g transform="translate(-16,20)">
			<text x="20" y="-5" class="name">
				{name}
				<!-- <tspan class="debug">({xpos}:{ypos},{w}x{h})</tspan> -->
			</text>
			<text x="20" y="15" class={"data-" + (level == 0 ? "root" : data.length == 64 ? "hash" : data !=0 ? "leaf":"empty")}>
				{ data.length>15 ? data.substring(0,15)+"..." : data }
			</text>
			{#if data!=0}
			<Icon name={icon} xpos={w - 15} ypos="-15" />
			{/if}
		</g>
	</g>
	
</svg>
