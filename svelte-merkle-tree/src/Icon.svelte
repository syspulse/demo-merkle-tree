<script context="module">
  import pathsByName from "./icons"
  export const iconNames = Object.keys(pathsByName)
  export const directions = ["n", "ne", "e", "se", "s", "sw", "w", "nw"]
</script>

<script>
  export let name = "arrow"
  export let direction = "n"
  export let xpos = 0
  export let ypos = 0

  $: paths = pathsByName[name] || []
  $: rotation = directions.indexOf(direction) * 45
</script>

<svg
  class="c"
  fill-rule="evenodd"
  clip-rule="evenodd"
  style="{`transform: transform: rotate(${rotation}deg)`}">
  <g transform="translate({xpos},{ypos})">
    {#each paths as path}
      <path d="{path}"></path>
    {/each}
  </g>
</svg>

<style>
  .c {
    width: 1.5em;
    height: 1em;
    fill: currentColor;
    /* fill: black; */
    transition: all 0.3s ease-out;
    overflow: visible;
  }
</style>
