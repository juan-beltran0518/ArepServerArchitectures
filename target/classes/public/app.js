document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('helloForm');
    const responseDiv = document.getElementById('response');
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        const name = document.getElementById('name').value;
        responseDiv.textContent = 'Cargando...';
        try {
            const res = await fetch(`/app/hello?name=${encodeURIComponent(name)}`);
            if (!res.ok) throw new Error('Error en la respuesta del servidor');
            const data = await res.json();
            responseDiv.textContent = data.mensaje;
        } catch (err) {
            responseDiv.textContent = 'Error: ' + err.message;
        }
    });
});
