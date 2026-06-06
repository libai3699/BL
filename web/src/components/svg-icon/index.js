const svgMap = import.meta.glob('/src/static/svgs/*.svg', {
  as: 'raw',
  eager: true
});

export function getSvg(name) {
  return svgMap[`/src/static/svgs/${name}.svg`] || '';
}
