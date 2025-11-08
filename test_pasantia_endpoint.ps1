# Test script for Pasantia registration endpoint
# Make sure the Spring Boot application is running on port 8080

Write-Host "Testing POST /pasantias/registrar endpoint..." -ForegroundColor Cyan

$jsonBody = @"
{
  "titulo": "Desarrollador Full Stack",
  "puestoACubrir": "Desarrollador Full Stack Junior",
  "ciudad": "Córdoba",
  "modalidad": "Híbrida",
  "asignacionEstimulo": 55000.0,
  "cantidadDePasantes": 2,
  "fechaPublicacion": "2025-01-15",
  "fechaCaducidad": "2025-04-15",
  "idEmpresa": 1,
  "idsCarreras": [6],
  "emailContacto": "rrhh@test.com",
  "conocimientos": "Java, Spring Boot, React, MySQL, Git",
  "otrosRequisitos": "Estudiante avanzado, disponibilidad part-time",
  "beneficios": "Capacitación, ambiente de trabajo dinámico, posibilidad de continuidad"
}
"@

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/pasantias/registrar" `
        -Method Post `
        -ContentType "application/json" `
        -Body $jsonBody `
        -ErrorAction Stop
    
    Write-Host "`n✅ SUCCESS!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Yellow
    $response | ConvertTo-Json -Depth 10
    
    if ($response.codigo -eq 0) {
        Write-Host "`n✅ Pasantía creada exitosamente!" -ForegroundColor Green
        Write-Host "ID de Pasantía: $($response.data.idPasantia)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "`n❌ ERROR:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.ErrorDetails.Message) {
        Write-Host "`nError Details:" -ForegroundColor Yellow
        $_.ErrorDetails.Message | ConvertFrom-Json | ConvertTo-Json -Depth 10
    }
}

Write-Host "`n---" -ForegroundColor Gray
Write-Host "To check the database, run:" -ForegroundColor Cyan
Write-Host "docker exec -it db_pasantias mysql -uroot -pmy-secret-pw db_pasantias -e 'SELECT * FROM Pasantia ORDER BY id_pasantia DESC LIMIT 1;'" -ForegroundColor White

